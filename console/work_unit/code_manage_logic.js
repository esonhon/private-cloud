/**
 * Created by strawmanbobi
 * 2016-11-27
 */

// system inclusion
fs = require('fs');
var crypto = require('crypto');

// global inclusion
require('../mini_poem/configuration/constants');
var orm = require('orm');
var AdminAuth = require('../authentication/admin_auth.js');
var PythonCaller = require('../mini_poem/external/python_caller');

var Category = require('../model/category_dao.js');
var Brand = require('../model/brand_dao.js');
var IRProtocol = require('../model/ir_protocol_dao.js');
var City = require('../model/city_dao.js');
var RemoteIndex = require('../model/remote_index_dao.js');
var StbOperator = require('../model/stb_operator_dao.js');

var RequestSender = require('../mini_poem/http/request.js');
var Map = require('../mini_poem/mem/map.js');

var Enums = require('../constants/enums.js');
var ErrorCode = require('../constants/error_code.js');
var Categories = require('../constants/remote_categories');

var logger = require('../mini_poem/logging/logger4js').helper;

var async = require('async');

var enums = new Enums();
var errorCode = new ErrorCode();

var adminAuth = new AdminAuth(REDIS_HOST, REDIS_PORT, null, REDIS_PASSWORD);

// relative XML file path
var PROTOCOL_PATH = "protocol";

var contributeProtocolService = "/irext-server/contribute/contribute_protocol";
var contributeBrandsService = "/irext-server/contribution/contribute_brands";
var contributeRemoteIndexesService = "/irext-server/contribution/contribute_remote_indexes";

exports.listCategoriesWorkUnit = function (from, count, callback) {
    var conditions = {
        status: enums.ITEM_VALID
    };
    Category.listRemoteCategories(conditions, from, count, "id", function(getCategoryByIDErr, categories) {
        callback(getCategoryByIDErr, categories);
    });
};

exports.listBrandsWorkUnit = function (categoryID, from, count, callback) {
    var conditions = {
        status: orm.gt(parseInt(enums.ITEM_INVALID)),
        category_id: categoryID
    };
    Brand.listBrands(conditions, from, count, "priority", function(getBrandByIDErr, brands) {
        callback(getBrandByIDErr, brands);
    });
};

exports.listUnpublishedBrandsWorkUnit = function (callback) {
    var conditions = {
        status: enums.ITEM_VERIFY
    };
    Brand.listBrands(conditions, 0, 100, "priority", function(getBrandByIDErr, brands) {
        callback(getBrandByIDErr, brands);
    });
};

exports.listProvincesWorkUnit = function (callback) {
    City.listProvinces(function(listProvincesErr, provinces) {
        callback(listProvincesErr, provinces);
    });
};

exports.listCitiesWorkUnit = function (provincePrefix, callback) {
    City.listCities(provincePrefix, function(listCitiesErr, cities) {
        callback(listCitiesErr, cities);
    });
};

exports.listOperatorsWorkUnit = function (cityCode, from, count, callback) {
    var conditions = {
        city_code: cityCode,
        status: enums.ITEM_VALID
    };
    StbOperator.listStbOperators(conditions, from, count, "id", function(listOperatorsErr, operators) {
        callback(listOperatorsErr, operators);
    });
};

exports.listRemoteIndexesWorkUnit = function (categoryID, brandID, cityCode, operatorID, from, count, callback) {
    var conditions;

    if (categoryID == enums.CATEGORY_STB) {
        if (null == operatorID) {
            conditions = {
                category_id: categoryID,
                city_code: cityCode,
                status: orm.gt(enums.ITEM_INVALID)
            };
        } else {
            conditions = {
                category_id: categoryID,
                city_code: cityCode,
                operator_id: operatorID,
                status: orm.gt(enums.ITEM_INVALID)
            };
        }

    } else if (categoryID == enums.CATEGORY_AC ||
        categoryID == enums.CATEGORY_TV ||
        categoryID == enums.CATEGORY_NW ||
        categoryID == enums.CATEGORY_IPTV ||
        categoryID == enums.CATEGORY_DVD ||
        categoryID == enums.CATEGORY_FAN ||
        categoryID == enums.CATEGORY_PROJECTOR ||
        categoryID == enums.CATEGORY_STEREO ||
        categoryID == enums.CATEGORY_LIGHT_BULB ||
        categoryID == enums.CATEGORY_BSTB ||
        categoryID == enums.CATEGORY_CLEANING_ROBOT ||
        categoryID == enums.CATEGORY_AIR_CLEANER ||
        categoryID == enums.CATEGORY_DYSON) {
        conditions = {
            category_id: categoryID,
            brand_id: brandID,
            status: orm.gt(enums.ITEM_INVALID)
        };
    } else {
        callback(errorCode.INVALID_CATEGORY, null);
        return;
    }

    RemoteIndex.listRemoteIndexes(conditions, from, count, "priority", function(listRemoteIndexesErr, remoteIndexes) {
        callback(listRemoteIndexesErr, remoteIndexes);
    });
};

exports.searchRemoteIndexesWorkUnit = function (remoteMap, from, count, callback) {
    var conditions = {
        remote_map: orm.like("%" + remoteMap + "%")
    };

    RemoteIndex.listRemoteIndexes(conditions, from, count, "priority", function(listRemoteIndexesErr, remoteIndexes) {
        callback(listRemoteIndexesErr, remoteIndexes);
    });
};

exports.downloadRemoteBinCachedWorkUnit = function(remoteIndexID, callback) {
    RemoteIndex.getRemoteIndexByID(remoteIndexID, function(getRemoteIndexErr, remoteIndex) {
        if (errorCode.SUCCESS.code == getRemoteIndexErr.code && null != remoteIndex) {
            var fileName = "irda_" + remoteIndex.protocol + "_" + remoteIndex.remote + ".bin";
            var localBinFileName = FILE_TEMP_PATH + "/" + fileName;

            var error = errorCode.SUCCESS;

            fs.exists(localBinFileName, function(exists) {
                if (exists) {
                    logger.info("file " + localBinFileName + " already exists, serve directly");
                    callback(error, localBinFileName);
                } else {
                    logger.info("file " + localBinFileName + " does not exist");
                    error = errorCode.FAILED;
                    callback(error, null);
                }
            });
        } else {
            logger.error("no valid remote index found by ID " + remoteIndexID);
            callback(errorCode.FAILED, null);
        }
    });
};

exports.listUnpublishedRemoteIndexesWorkUnit = function (callback) {
    var conditions = {
        status: enums.ITEM_PASS
    };

    RemoteIndex.listRemoteIndexes(conditions, 0, 1000, "priority", function(listRemoteIndexesErr, remoteIndexes) {
        callback(listRemoteIndexesErr, remoteIndexes);
    });
};

exports.listIRProtocolsWorkUnit = function (from, count, callback) {
    var conditions = {
        status: orm.gt(enums.ITEM_INVALID)
    };
    IRProtocol.listIRProtocols(conditions, from, count, "name", function(listIRProtocolsErr, IRProtocols) {
        callback(listIRProtocolsErr, IRProtocols);
    });
};

exports.createRemoteIndexWorkUnit = function(remoteIndex, filePath, contentType, adminID, callback) {
    //////////////////////////////////////
    // step 1, rename input remote xml file
    var find = '\\\\';
    var re = new RegExp(find, 'g');
    var unixFilePath = filePath.replace(re, '/');
    var lios = unixFilePath.lastIndexOf('/');
    var fileDir = unixFilePath.substring(0, lios);
    var subCate = remoteIndex.sub_cate;
    var categoryID = remoteIndex.category_id;
    var remoteDir = "";
    var remoteXMLFilePath;
    var remoteBinFilePath;
    var protocolPath;
    var outputPath;
    var outputFilePath;
    var newRemoteIndex;
    var newACRemoteNumber;
    var tagType;
    var contributor = "";
    var protocolFileName = "";
    var localProtocolFileName = "";

    var pythonRuntimeDir = null,
        pythonFile = null,
        userArgs = [];

    // verify admin
    var key = "admin_name_" + adminID;
    adminAuth.getAuthInfo(key, function(getAdminAuthErr, result) {
        if (errorCode.SUCCESS.code == getAdminAuthErr.code && null != result) {
            contributor = result;

            // begin creating remote index
            switch(parseInt(categoryID)) {
                case enums.CATEGORY_AC:
                    pythonFile = "ir_status_encode.py";
                    break;
                case enums.CATEGORY_TV:
                case enums.CATEGORY_STB:
                case enums.CATEGORY_NW:
                case enums.CATEGORY_IPTV:
                case enums.CATEGORY_DVD:
                case enums.CATEGORY_FAN:
                case enums.CATEGORY_PROJECTOR:
                case enums.CATEGORY_STEREO:
                case enums.CATEGORY_LIGHT_BULB:
                case enums.CATEGORY_BSTB:
                case enums.CATEGORY_CLEANING_ROBOT:
                case enums.CATEGORY_AIR_CLEANER:
                case enums.CATEGORY_DYSON:
                    pythonFile = "ir_command_encode.py";
                    break;
                default:
                    logger.error("no remote category found!!");
                    callback(errorCode.FAILED, null);
                    break;
            }

            logger.info("encoding python file = " +
                pythonFile);

            // process xml source file and source remote indexes
            if (parseInt(categoryID) == enums.CATEGORY_AC) {
                // TAG FORMAT encode
                tagType = "new_ac";
                remoteXMLFilePath = fileDir + "/" + remoteIndex.remote_name + ".xml";
                // logger.info("remote XML file path = " + remoteXMLFilePath);
                //////////////////////////////////////
                // step 2, parse python run-time path, AC python file name and user arguments

                pythonRuntimeDir = fileDir + "/" + remoteDir;
                userArgs.length = 0;
                // python s_$category.py [remote_xml_file_abs_file] [remote_xml_file_name] [remote_xml_dir_abs_path]
                userArgs.push(remoteXMLFilePath);
                // set the xml directory as output path
                userArgs.push(fileDir + "/");
                pythonCaller = new PythonCaller();
                try {
                    pythonCaller.call(pythonRuntimeDir, pythonFile, userArgs, function(remoteGenErr, genResult) {
                        if (errorCode.SUCCESS.code == remoteGenErr) {
                            //////////////////////////////////////
                            // step 3, if successfully created tag binary file, upload binary
                            logger.info("remote " + remoteIndex.remote_name + " has successfully been generated");
                            outputPath = fileDir;
                            newACRemoteNumber = remoteIndex.remote_name
                                .substring(remoteIndex.remote_name.lastIndexOf('_') + 1);
                            outputFilePath = outputPath + "/irda_" + tagType + "_" + newACRemoteNumber + ".bin";
                            logger.info("final tag binary output file = " + outputFilePath);

                            fs.readFile(outputFilePath, function(readFileErr, fileData) {
                                if (readFileErr) {
                                    logger.error("read remote code binary file error : " + readFileErr);
                                    callback(errorCode.FAILED, null);
                                } else {
                                    logger.info("read remote binary file successfully, file size = " + fileData.length);

                                    //////////////////////////////////////
                                    // step 3.5, check if this remote index is already
                                    //  contained in remote index list by binary
                                    var fileHash = checksum(fileData);
                                    logger.info("hash of binary file = " + fileHash);
                                    var conditions = {
                                        category_id: remoteIndex.category_id,
                                        brand_id: remoteIndex.brand_id,
                                        binary_md5: fileHash
                                    };

                                    RemoteIndex.findRemoteIndexByCondition(conditions,
                                        function(findRemoteIndexesErr, remoteIndexes) {
                                            if (errorCode.SUCCESS.code == findRemoteIndexesErr.code &&
                                                null != remoteIndexes && remoteIndexes.length > 0) {
                                                logger.info("this remote is duplicated by binary value");
                                                callback(errorCode.DUPLICATED_REMOTE_CODE, null);
                                            } else {
                                                // step 4, create remote index record in db
                                                remoteIndex.remote_name = newACRemoteNumber + "";

                                                newRemoteIndex = {
                                                    category_id: remoteIndex.category_id,
                                                    category_name: remoteIndex.category_name,
                                                    brand_id: remoteIndex.brand_id,
                                                    brand_name: remoteIndex.brand_name,
                                                    protocol: tagType,
                                                    remote: remoteIndex.remote_name,
                                                    remote_map: tagType + "_" + newACRemoteNumber,
                                                    priority: remoteIndex.priority,
                                                    sub_cate: subCate,
                                                    remote_number: remoteIndex.remote_number,
                                                    category_name_tw: remoteIndex.category_name_tw,
                                                    brand_name_tw: remoteIndex.brand_name_tw,
                                                    binary_md5: fileHash,
                                                    contributor: contributor
                                                };

                                                // see if this remote index is already in database
                                                var conditions = {
                                                    //category_id: remoteIndex.category_id,
                                                    //brand_id: remoteIndex.brand_id,
                                                    protocol: tagType,
                                                    remote: remoteIndex.remote_name,
                                                    status: orm.gt(enums.ITEM_INVALID)
                                                };

                                                RemoteIndex.findRemoteIndexByCondition(conditions,
                                                    function(findRemoteIndexErr, remoteIndexes) {
                                                        if(errorCode.SUCCESS.code == findRemoteIndexErr.code &&
                                                            remoteIndexes &&
                                                            remoteIndexes.length > 0) {
                                                            logger.info("remote index already exists");

                                                            callback(errorCode.FAILED, null);
                                                        } else {
                                                            RemoteIndex.createRemoteIndex(newRemoteIndex,
                                                                function(createRemoteIndexErr, createdRemoteIndex) {
                                                                    callback(createRemoteIndexErr, createdRemoteIndex);
                                                                });
                                                        }
                                                    });
                                            }
                                        });
                                }
                            });
                        } else {
                            logger.info("remote " + remoteIndex.remote_name + " generating failed");
                            callback(errorCode.FAILED, null);
                        }
                    });
                } catch (exception) {
                    logger.error('failed to execute python script from application');
                    callback(errorCode.FAILED, null);
                }
            } else {
                // P-R FORMAT encode
                //////////////////////////////////////
                // step 1.5, download specified protocol binary from local file storage
                protocolPath = fileDir + "/" + PROTOCOL_PATH + "/";
                protocolFileName = remoteIndex.protocol_name + ".bin";
                localProtocolFileName = protocolPath + remoteIndex.protocol_name + ".bin";
                logger.info("protocol binary fetched from file storage, continue processing with remote file");
                remoteXMLFilePath = fileDir + "/" + remoteIndex.remote_name + ".xml";
                logger.info("remote XML file path = " + remoteXMLFilePath);
                //////////////////////////////////////
                // step 2, parse python run-time path, python file name and user arguments
                pythonRuntimeDir = fileDir + "/" + remoteDir;
                userArgs.length = 0;
                // python s_$category.py [remote_xml_file_abs_file] [remote_xml_file_name] [remote_xml_dir_abs_path]
                userArgs.push(remoteXMLFilePath);
                userArgs.push(remoteIndex.remote_name + ".xml");
                userArgs.push(fileDir + "/");
                userArgs.push(categoryID);

                //////////////////////////////////////
                // step 3, try executing remote encoding script
                var pythonCaller = new PythonCaller();
                try {
                    pythonCaller.call(pythonRuntimeDir, pythonFile, userArgs, function(remoteGenErr, genResult) {
                        if(errorCode.SUCCESS.code == remoteGenErr) {

                            //////////////////////////////////////
                            // step 4, try executing merge script
                            logger.info("remote " + remoteIndex.remote_name + " has successfully been generated." +
                                " continue merging with protocol");
                            pythonFile = "ir_command_merge.py";
                            outputPath = fileDir;
                            remoteBinFilePath = fileDir + "/" + remoteDir + remoteIndex.protocol_name + "#" +
                                remoteIndex.remote_name + ".bin";
                            userArgs.length = 0;
                            // python ir_command_merge.py [protocol_dir_abs_path]
                            // [remote_bin_file_abs_path] [output_$category_dir_abs_path]
                            logger.info("protocol path = " + protocolPath + ", remote bin path = " + remoteBinFilePath +
                                ", output = " + outputPath);
                            userArgs.push(protocolPath);
                            userArgs.push(remoteBinFilePath);
                            userArgs.push(outputPath);
                            pythonCaller.call(pythonRuntimeDir, pythonFile, userArgs,
                                function(remoteMergeErr, mergeResult) {
                                logger.info("merge protocol error = " + remoteMergeErr);
                                if(errorCode.SUCCESS.code == remoteMergeErr) {
                                    outputFilePath = outputPath + "/irda_" + remoteIndex.protocol_name + "_" +
                                        remoteIndex.remote_name + ".bin";
                                    logger.info("final output file = " + outputFilePath);

                                    fs.readFile(outputFilePath, function(readFileErr, fileData) {
                                        if (readFileErr) {
                                            logger.error("read remote code binary file error : " + readFileErr);
                                            callback(errorCode.FAILED, null);
                                        } else {
                                            logger.info("read remote binary file successfully, " +
                                                "file size = " + fileData.length);
                                            //////////////////////////////////////
                                            // step 5, check if this remote index is already contained in remote index
                                            // list by binary
                                            var fileHash = checksum(fileData);
                                            logger.info("hash of binary file = " + fileHash);
                                            var conditions = null;

                                            if (enums.CATEGORY_STB == remoteIndex.category_id) {
                                                conditions = {
                                                    category_id: remoteIndex.category_id,
                                                    city_code: remoteIndex.city_code,
                                                    binary_md5: fileHash
                                                };
                                            } else {
                                                conditions = {
                                                    category_id: remoteIndex.category_id,
                                                    brand_id: remoteIndex.brand_id,
                                                    binary_md5: fileHash
                                                };
                                            }

                                            RemoteIndex.findRemoteIndexByCondition(conditions,
                                            function(findRemoteIndexesErr, remoteIndexes) {
                                                if (errorCode.SUCCESS.code == findRemoteIndexesErr.code &&
                                                    null != remoteIndexes && remoteIndexes.length > 0) {
                                                    logger.info("this remote is duplicated by binary value");
                                                    callback(errorCode.DUPLICATED_REMOTE_CODE, null);
                                                } else {
                                                    //////////////////////////////////////
                                                    // step 6, create remote index record in db
                                                    if (remoteIndex.category_id == enums.CATEGORY_STB) {
                                                        newRemoteIndex = {
                                                            category_id: remoteIndex.category_id,
                                                            category_name: remoteIndex.category_name,
                                                            city_code: remoteIndex.city_code,
                                                            city_name: remoteIndex.city_name,
                                                            operator_id: remoteIndex.operator_id,
                                                            operator_name: remoteIndex.operator_name,
                                                            protocol: remoteIndex.protocol_name,
                                                            remote: remoteIndex.remote_name,
                                                            remote_map: remoteIndex.protocol_name +
                                                            '_' + remoteIndex.remote_name,
                                                            priority: remoteIndex.priority,
                                                            sub_cate: subCate,
                                                            remote_number: remoteIndex.remote_number,
                                                            city_name_tw: remoteIndex.city_name_tw,
                                                            operator_name_tw: remoteIndex.operator_name_tw,
                                                            binary_md5: fileHash,
                                                            contributor: contributor
                                                        }
                                                    } else {
                                                        newRemoteIndex = {
                                                            category_id: remoteIndex.category_id,
                                                            category_name: remoteIndex.category_name,
                                                            brand_id: remoteIndex.brand_id,
                                                            brand_name: remoteIndex.brand_name,
                                                            protocol: remoteIndex.protocol_name,
                                                            remote: remoteIndex.remote_name,
                                                            remote_map: remoteIndex.protocol_name +
                                                            '_' + remoteIndex.remote_name,
                                                            priority: remoteIndex.priority,
                                                            sub_cate: subCate,
                                                            remote_number: remoteIndex.remote_number,
                                                            category_name_tw: remoteIndex.category_name_tw,
                                                            brand_name_tw: remoteIndex.brand_name_tw,
                                                            binary_md5: fileHash,
                                                            contributor: contributor
                                                        }
                                                    }

                                                    // see if this remote index is already in database
                                                    var conditions = {
                                                        // category_id: remoteIndex.category_id,
                                                        // brand_id: remoteIndex.brand_id,
                                                        protocol: remoteIndex.protocol_name,
                                                        remote: remoteIndex.remote_name,
                                                        status: orm.gt(enums.ITEM_INVALID)
                                                    };

                                                    RemoteIndex.findRemoteIndexByCondition(conditions,
                                                        function(findRemoteIndexErr, remoteIndexes) {
                                                        if(errorCode.SUCCESS.code == findRemoteIndexErr.code &&
                                                        remoteIndexes &&
                                                        remoteIndexes.length > 0) {
                                                            logger.info("remote index already exists");
                                                            callback(errorCode.FAILED, null);
                                                        } else {
                                                            RemoteIndex.createRemoteIndex(newRemoteIndex,
                                                            function(createRemoteIndexErr, createdRemoteIndex) {
                                                                callback(createRemoteIndexErr, createdRemoteIndex);
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    callback(errorCode.FAILED, null);
                                }
                            });
                        } else {
                            callback(errorCode.FAILED, null);
                        }
                    });
                } catch (exception) {
                    logger.error('failed to execute python script from application');
                    callback(errorCode.FAILED, null);
                }
            }
        } else {
            logger.info("invalid admin ID, return directly");
            callback(errorCode.FAILED, null);
        }
    });
};

exports.deleteRemoteIndexWorkUnit = function (remoteIndex, adminID, callback) {
    var key = "admin_name_" + adminID;
    adminAuth.getAuthInfo(key, function(getAdminAuthErr, result) {
        if (errorCode.SUCCESS.code == getAdminAuthErr.code && null != result) {
            if(remoteIndex.contributor.indexOf(result) == -1) {
                logger.info("the admin " + result + " could not change this remote index");
                callback(errorCode.FAILED);
                return;
            }
            key = "admin_" + adminID;
            adminAuth.getAuthInfo(key, function(getAdminAuthErr, result) {
                if (errorCode.SUCCESS.code == getAdminAuthErr.code && null != result) {
                    callback(errorCode.SUCCESS);
                } else {
                    callback(errorCode.FAILED);
                }
            });
        } else {
            callback(errorCode.FAILED);
        }
    });
};

exports.verifyRemoteIndexWorkUnit = function (remoteIndex, pass, adminID, callback) {
    var key = "admin_name_" + adminID;
    adminAuth.getAuthInfo(key, function(getAdminAuthErr, result) {
        if (errorCode.SUCCESS.code == getAdminAuthErr.code && null != result) {
            if(remoteIndex.contributor.indexOf(result) == -1) {
                logger.info("the admin " + result + " could not change this remote index");
                callback(errorCode.FAILED);
                return;
            }
            var status = 0 == pass ? enums.ITEM_PASS : enums.ITEM_FAILED;

            RemoteIndex.verifyRemoteIndex(remoteIndex.id, status, function(updateRemoteIndexErr) {
                callback(updateRemoteIndexErr);
            });
        } else {
            callback(errorCode.FAILED);
        }
    });
};

exports.fallbackRemoteIndexWorkUnit = function (remoteIndex, adminID, callback) {
    var key = "admin_name_" + adminID;
    adminAuth.getAuthInfo(key, function(getAdminAuthErr, result) {
        if (errorCode.SUCCESS.code == getAdminAuthErr.code && null != result) {
            if (remoteIndex.contributor.indexOf(result) == -1) {
                logger.info("the admin " + result + " could not change this remote index");
                callback(errorCode.FAILED);
                return;
            }
            var status = enums.ITEM_VERIFY;

            RemoteIndex.fallbackRemoteIndex(remoteIndex.id, status, function (updateRemoteIndexErr) {
                callback(updateRemoteIndexErr);
            });
        } else {
            callback(errorCode.FAILED);
        }
    });
};

exports.publishRemoteIndexWorkUnit = function (callback) {
    callback(errorCode.SUCCESS);
};

exports.createBrandWorkUnit = function (brand, adminID, callback) {
    var conditions = {
        category_id: brand.category_id,
        name: brand.name,
        status: enums.ITEM_VERIFY
    };

    var key = "admin_name_" + adminID;
    adminAuth.getAuthInfo(key, function(getAdminAuthErr, result) {
        if (getAdminAuthErr.code == errorCode.SUCCESS.code &&
            null != result) {
            brand.contributor = result;
            Brand.findBrandByConditions(conditions, function(findBrandErr, brands) {
                if(errorCode.SUCCESS.code == findBrandErr.code && null != brands && brands.length > 0) {
                    logger.info("brand already exists");
                    callback(errorCode.SUCCESS);
                } else {
                    Brand.createBrand(brand, function(createBrandErr, createdBrand) {
                        callback(createBrandErr, createdBrand);
                    });
                }
            });
        } else {
            callback(errorCode.FAILED, null);
        }
    });
};

exports.publishBrandsWorkUnit = function (adminID, callback) {
    var conditions = null;
    var key = "admin_name_" + adminID;
    adminAuth.getAuthInfo(key, function(getAdminAuthErr, result) {
        if (getAdminAuthErr.code == errorCode.SUCCESS.code &&
            null != result) {
            conditions = {
                status: enums.ITEM_VERIFY
            };
            Brand.findBrandByConditions(conditions, function (findBrandErr, brands) {
                if (errorCode.SUCCESS.code === findBrandErr.code && null !== brands && brands.length > 0) {
                    logger.info("unpublished brand list has been found");
                    async.eachSeries(brands, function (brand, innerCallback) {
                        brand.status = enums.ITEM_VALID;
                        Brand.updateBrandByID(brand.id, brand, function (updateBrandErr, updatedBrand) {
                            innerCallback();
                        });
                    }, function (err) {
                        // send HTTP request to IRext main server
                        var queryParams = new Map();
                        var requestSender =
                            new RequestSender(EXTERNAL_SERVER_ADDRESS,
                                EXTERNAL_SERVER_PORT,
                                contributeBrandsService,
                                queryParams);
                        for (var i = 0; i < brands.length; i++) {
                            brands[i].status = enums.ITEM_VERIFY;
                        }
                        var contributeBrandsRequest = {
                            brandList : brands
                        };
                        requestSender.sendPostRequest(contributeBrandsRequest,
                            function(contributeBrandsRequestErr, contributeBrandsResponse) {
                                logger.info(contributeBrandsRequestErr);
                                callback(errorCode.SUCCESS);
                            });
                        callback(errorCode.SUCCESS);
                    });
                } else {
                    callback(errorCode.SUCCESS);
                }
            });
        } else {
            callback(errorCode.FAILED);
        }
    });
};

exports.createProtocolWorkUnit = function(protocol, filePath, contentType, adminID, callback) {
    //////////////////////////////////////
    // step 1, rename input remote xml file
    var find = '\\\\';
    var re = new RegExp(find, 'g');
    var unixFilePath = filePath.replace(re, '/');
    var lios = unixFilePath.lastIndexOf('/');
    var fileDir = unixFilePath.substring(0, lios);
    var contributor;

    var protocolName = protocol.protocol_name_b;
    var srcFile = fileDir + "/" + protocolName + ".xml";
    var destFile = fileDir + "/" + protocolName + ".bin";
    var protocolType = protocol.protocol_type;
    var localProtocolFile = "";

    var pythonRuntimeDir = fileDir,
        pythonFile = "ir_command_protocol.py",
        userArgs = [];

    /////////////////////////////////////
    // step 2, get admin name as contributor
    var key = "admin_name_" + adminID;
    adminAuth.getAuthInfo(key, function(getAdminAuthErr, result) {
        if (errorCode.SUCCESS.code == getAdminAuthErr.code && null != result) {
            contributor = result;
            if (enums.PROTOCOL_TYPE_G2_QUATERNARY == protocolType) {
                pythonFile = "ir_command_protocol.py";
            } else if (enums.PROTOCOL_TYPE_G2_HEXDECIMAL == protocolType) {
                pythonFile = "ir_command_protocol_hex.py";
            }

            logger.info("prepare to parse protocol");
            userArgs.length = 0;
            // python s_$category.py [remote_xml_file_abs_file] [remote_xml_file_name] [remote_xml_dir_abs_path]
            userArgs.push(srcFile);
            userArgs.push(destFile);

            //////////////////////////////////////
            // step 3, try executing remote encoding
            var pythonCaller = new PythonCaller();
            try {
                logger.info("prepare to call python script");
                pythonCaller.call(pythonRuntimeDir, pythonFile, userArgs, function(protocolGenErr, genResult) {
                    if(errorCode.SUCCESS.code == protocolGenErr) {
                        //////////////////////////////////////
                        // step 3.5, upload protocol binary file to local file storage
                        localProtocolFile = destFile;
                        fs.readFile(localProtocolFile, function(readFileErr, fileData) {
                            if (readFileErr) {
                                logger.error("read protocol binary file error : " + readFileErr);
                                callback(errorCode.FAILED);
                            } else {
                                logger.info("read protocol binary successfully, file size = " + fileData.length);
                                //////////////////////////////////////
                                // step 4, try register protocol to db
                                var newProtocol = {
                                    name: protocolName,
                                    status: enums.ITEM_VALID,
                                    type: protocolType,
                                    contributor: contributor
                                };

                                var conditions = {
                                    name: protocolName
                                };

                                logger.info("ir_command_protocol.py called successfully, create protocol in DB");
                                IRProtocol.findIRProtocolByConditions(conditions,
                                    function(findIRProtocolErr, IRProtocols) {
                                        if(errorCode.SUCCESS.code == findIRProtocolErr.code &&
                                            null != IRProtocols &&
                                            IRProtocols.length > 0) {
                                            logger.info("protocol " + protocolName + " already exists, " +
                                                "nothing to be updated");
                                            callback(errorCode.SUCCESS);
                                        } else {
                                            IRProtocol.createIRProtocol(newProtocol,
                                                function(createIRProtocolErr, createdIRProtocol) {
                                                    callback(createIRProtocolErr);
                                                });
                                        }
                                    });
                                // contribute protocol to IRext main server
                                var queryParams = new Map();
                                var requestSender =
                                    new RequestSender(EXTERNAL_SERVER_ADDRESS,
                                        EXTERNAL_SERVER_PORT,
                                        contributeProtocolService,
                                        queryParams);
                                newProtocol.status = enums.ITEM_VERIFY;
                                var contributeProtocolRequest = {
                                    protocol : newProtocol
                                };
                                var formData = {
                                    // Pass a simple key-value pair
                                    protocol: JSON.stringify(contributeProtocolRequest),
                                    protocolFile: fs.createReadStream(localProtocolFile),
                                };
                                requestSender.postMultipartForm(formData,
                                    function(contributeProtocolRequestErr, contributeProtocolResponse) {
                                        logger.info(contributeProtocolRequestErr);
                                        callback(errorCode.SUCCESS);
                                    });
                            }
                        });
                    } else {
                        logger.error("ir_command_protocol.py called failed : " + protocolGenErr);
                        callback(errorCode.FAILED);
                    }
                });
            } catch (exception) {
                logger.error('failed to execute python script from application');
                callback(errorCode.FAILED);
            }
        } else {
            callback(errorCode.FAILED);
        }
    });
};

// Ultilities
function checksum(str, algorithm, encoding) {
    return crypto
        .createHash(algorithm || 'md5')
        .update(str, 'utf8')
        .digest(encoding || 'hex')
}
