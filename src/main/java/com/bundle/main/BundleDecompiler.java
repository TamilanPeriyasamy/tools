package com.bundle.main;

import com.bundle.decompile.DecompileManger;
import com.bundle.exception.EncodeDecodeException;
import com.bundle.utils.FilePaths;
import com.bundle.utils.LogFileGenerator;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class BundleDecompiler {

    public static String mOutput_File_Dir = null;
    public static String mInput_File_Dir = null;
    public static boolean mEnableXmlDecode = true;
    public static boolean mEnableSourceDecode = true;
    public static boolean mDebugMode = true;
    public static boolean mBundleDecompile = false;
    public static boolean mBundleBuild = false;


    public static void main(String args[]) throws Exception {
        LogFileGenerator.toStartWriteLogFile();
        if (args.length != 3) {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.ARGS_MISMATCHED);
        }
        String decodeEncodeMode = args[0];
        String mInputFileDir    = args[1];
        String mOutputFileDir   = args[2];

        decodeEncodeMode = "decompile";
        mInputFileDir    = "--in=/home/lakeba13/MyDev/App_Bundles/MyApp.aab";
        mOutputFileDir   = "--out=/home/lakeba13/workspace/LakebaSecurityFrameworkMobile/BundleDecompiler/debug";
        doRunBundleDecompiler(decodeEncodeMode, mInputFileDir, mOutputFileDir);

        decodeEncodeMode = "build";
        mInputFileDir = "--in=/home/lakeba13/workspace/LakebaSecurityFrameworkMobile/BundleDecompiler/debug/MyApp";
        mOutputFileDir = "--out=/home/lakeba13/workspace/LakebaSecurityFrameworkMobile/BundleDecompiler/output/MyApp.aab";
        doRunBundleDecompiler(decodeEncodeMode, mInputFileDir, mOutputFileDir);

        LogFileGenerator.toStopWriteLogFile();
    }

    private static void doRunBundleDecompiler(String decodeEncodeMode, String mInputFileDir, String mOutputFileDir) throws Exception {


        if (decodeEncodeMode.trim().equals("decompile") || decodeEncodeMode.trim().equals("d")) { // build or decompile
            mBundleDecompile = true;
            mBundleBuild =false;
        } else if (decodeEncodeMode.trim().equals("build") || decodeEncodeMode.trim().equals("b")) { // build or decompile
            mBundleBuild = true;
            mBundleDecompile=false;
        } else {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
        }

        if (!mInputFileDir.trim().startsWith("--in=")) {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
        }
        if (mInputFileDir.trim().startsWith("--in=")) { // mInputFile or mInputDir
            mInput_File_Dir = mInputFileDir.substring(mInputFileDir.indexOf("--in=") + 5).trim();
            if (!new File(mInput_File_Dir).exists()) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }
            if (mBundleDecompile && !new File(mInput_File_Dir).isFile()) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }
            if (mBundleBuild && !new File(mInput_File_Dir).isDirectory()) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

        } else {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
        }

        if (!mOutputFileDir.trim().startsWith("--out=")) {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
        }

        if (mOutputFileDir.trim().startsWith("--out=")) { // mOutputFile or mOutputDir
            mOutput_File_Dir = mOutputFileDir.substring(mOutputFileDir.indexOf("--out=") + 6).trim();
            String mOutputDirPath = new File(mOutput_File_Dir).getParent();
            if (mBundleDecompile && !new File(mOutput_File_Dir).isDirectory()) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

            if (mBundleBuild && !new File(mOutputDirPath).exists()) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

            if (mBundleBuild && !mOutput_File_Dir.endsWith(".aab")) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

        } else {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
        }


        BundleAnalyze bundleAnalyze = new BundleAnalyze(mInput_File_Dir, mOutput_File_Dir);
        bundleAnalyze.analyze();
        if (mBundleDecompile) {
            System.out.println("\nInput App Bundle path: "+mInput_File_Dir);
            DecompileManger decompiler = new DecompileManger();
            decompiler.decompile();
            FileUtils.deleteDirectory(new File(FilePaths.mTempDirPath));
        }
    }
}