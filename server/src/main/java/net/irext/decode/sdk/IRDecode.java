package net.irext.decode.sdk;

import net.irext.server.sdk.bean.ACStatus;
import net.irext.server.sdk.bean.TemperatureRange;
import net.irext.server.sdk.utils.Constants;
import net.irext.server.service.utils.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;

/**
 * Filename:       IRDecode.java
 * Revised:        Date: 2017-04-22
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Wrapper-sdk of IR server
 * <p>
 * Revision log:
 * 2017-04-23: created by strawmanbobi
 */
public class IRDecode {

    private static final String TAG = IRDecode.class.getSimpleName();

    @Autowired
    private static ServletContext context;

    private static Object mSync = new Object();

    private native int irOpen(int category, int subCate, String fileName);

    private native int irOpenBinary(int category, int subCate, byte[] binaries, int binLength);

    private native int[] irDecode(int keyCode, ACStatus acStatus, int changeWindDirection);

    private native void irClose();

    private native int[] irDecodeCombo(int category, int subCate, byte[] binaries, int binLength,
                                       int keyCode, ACStatus acStatus, int changeWindDirection);

    private native TemperatureRange irACGetTemperatureRange(int acMode);

    private native int irACGetSupportedMode();

    private native int irACGetSupportedWindSpeed(int acMode);

    private native int irACGetSupportedSwing(int acMode);

    private native int irACGetSupportedWindDirection(int acMode);

    private static IRDecode mInstance;

    public static IRDecode getInstance() {
        if (null == mInstance) {
            mInstance = new IRDecode();
        }
        return mInstance;
    }

    private IRDecode() {
        String libPath = "/data/irext/libirda_decoder.so";
        LoggerUtil.getInstance().trace(TAG, "loading server library " + libPath);
        System.load(libPath);
    }

    public int openFile(int category, int subCate, String fileName) {
        return irOpen(category, subCate, fileName);
    }

    public int openBinary(int category, int subCate, byte[] binaries, int binLength) {
        return irOpenBinary(category, subCate, binaries, binLength);
    }

    public int[] decodeBinary(int keyCode, ACStatus acStatus, int changeWindDir) {
        int[] decoded;
        synchronized (mSync) {
            if (null == acStatus) {
                LoggerUtil.getInstance().trace(TAG, "AC Status is null, create a default one");
                acStatus = new ACStatus();
            } else {
                LoggerUtil.getInstance().trace(TAG, "AC Status = " +
                        acStatus.getAcPower() + ", " + acStatus.getAcMode() +
                        ", " + acStatus.getAcTemp() + ", " + acStatus.getAcWindSpeed() +
                        ", " + acStatus.getAcWindDir() + ", keyCode = " + keyCode);
            }
            decoded = irDecode(keyCode, acStatus, changeWindDir);
        }
        return decoded;
    }

    public int[] decodeBinary(int category, int subCate, byte[] binaries, int binLength,
            int keyCode, ACStatus acStatus, int changeWindDir) {
        int[] decoded;
        synchronized (mSync) {
            if (null == acStatus) {
                LoggerUtil.getInstance().trace(TAG, "AC Status is null, create a default one");
                acStatus = new ACStatus();
            } else {
                LoggerUtil.getInstance().trace(TAG, "AC Status = " +
                        acStatus.getAcPower() + ", " + acStatus.getAcMode() +
                        ", " + acStatus.getAcTemp() + ", " + acStatus.getAcWindSpeed() +
                        ", " + acStatus.getAcWindDir() + ", keyCode = " + keyCode);
            }
            decoded = irDecodeCombo(category, subCate, binaries, binLength,
                    keyCode, acStatus, changeWindDir);
        }
        return decoded;
    }

    public void closeBinary() {
        irClose();
    }

    public TemperatureRange getTemperatureRange(int acMode) {
        return irACGetTemperatureRange(acMode);
    }

    public int[] getACSupportedMode() {
        // cool, heat, auto, fan, de-humidification
        int[] retSupportedMode = {0, 0, 0, 0, 0};
        int supportedMode = irACGetSupportedMode();
        for (int i = Constants.ACMode.MODE_COOL.getValue(); i <=
                Constants.ACMode.MODE_DEHUMIDITY.getValue(); i++) {
            retSupportedMode[i] = (supportedMode >>> 1) & 1;
        }
        return retSupportedMode;
    }

    public int[] getACSupportedWindSpeed(int acMode) {
        // auto, low, medium, high
        int[] retSupportedWindSpeed = {0, 0, 0, 0};
        int supportedWindSpeed = irACGetSupportedWindSpeed(acMode);
        for (int i = Constants.ACWindSpeed.SPEED_AUTO.getValue();
             i <= Constants.ACWindSpeed.SPEED_HIGH.getValue();
             i++) {
            retSupportedWindSpeed[i] = (supportedWindSpeed >>> 1) & 1;
        }
        return retSupportedWindSpeed;
    }

    public int[] getACSupportedSwing(int acMode) {
        // swing-on, swing-off
        int[] retSupportedSwing = {0, 0};
        int supportedSwing = irACGetSupportedSwing(acMode);
        for (int i = Constants.ACSwing.SWING_ON.getValue();
             i <= Constants.ACSwing.SWING_OFF.getValue();
             i++) {
            retSupportedSwing[i] = (supportedSwing >>> 1) & 1;
        }
        return retSupportedSwing;
    }

    public int getACSupportedWindDirection(int acMode) {
        // how many directions supported by specific AC
        return irACGetSupportedWindDirection(acMode);
    }
}
