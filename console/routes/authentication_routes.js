/**
 * Created by strawmanbobi
 * 2016-11-27
 */

var app = require('../irext_console.js');
var authenticationService = require('../services/authentication_service.js');

app.post('/irext/certificate/admin_login', authenticationService.adminLogin);
app.post('/irext/certificate/token_verify', authenticationService.verifyToken);
app.post('/irext/certificate/change_pw', authenticationService.changePassword);