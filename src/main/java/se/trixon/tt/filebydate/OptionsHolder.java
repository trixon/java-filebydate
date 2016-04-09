/* 
 * Copyright 2016 Patrik Karlsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.tt.filebydate;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.text.SimpleDateFormat;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FilenameUtils;
import se.trixon.tt.filebydate.Operation.Command;

/**
 *
 * @author Patrik Karlsson
 */
public class OptionsHolder {

    private Command mCommand;
    private SimpleDateFormat mDateFormat;

    private String mDatePattern;
    private DateSource mDateSource;
    private String mDateSourceString;
    private File mDestDir;
    private boolean mDryRun;
    private String mFilePattern;
    private boolean mFollowLinks;
    private boolean mModeCopy;
    private boolean mModeMove;
    private PathMatcher mPathMatcher;
    private boolean mRecursive;
    private File mSourceDir;
    private final StringBuilder mValidationErrorBuilder = new StringBuilder();

    public OptionsHolder(CommandLine commandLine) {
        mModeCopy = commandLine.hasOption("copy");
        mModeMove = commandLine.hasOption("move");

        mDatePattern = commandLine.getOptionValue("dp");
        mDateSourceString = commandLine.getOptionValue("ds");
        //mFilePattern = commandLine.getOptionValue("fp");

        mDryRun = commandLine.hasOption("dry-run");
        mFollowLinks = commandLine.hasOption("links");
        mRecursive = commandLine.hasOption("recursive");

        setSourceAndDest(commandLine.getArgs());
    }

    public Command getCommand() {
        return mCommand;
    }

    public SimpleDateFormat getDateFormat() {
        return mDateFormat;
    }

    public String getDatePattern() {
        return mDatePattern;
    }

    public DateSource getDateSource() {
        return mDateSource;
    }

    public String getDateSourceString() {
        return mDateSourceString;
    }

    public File getDestDir() {
        return mDestDir;
    }

    public String getFilePattern() {
        return mFilePattern;
    }

    public PathMatcher getPathMatcher() {
        return mPathMatcher;
    }

    public File getSourceDir() {
        return mSourceDir;
    }

    public String getValidationError() {
        return mValidationErrorBuilder.toString();
    }

    public boolean isDryRun() {
        return mDryRun;
    }

    public boolean isFollowLinks() {
        return mFollowLinks;
    }

    public boolean isModeCopy() {
        return mModeCopy;
    }

    public boolean isModeMove() {
        return mModeMove;
    }

    public boolean isRecursive() {
        return mRecursive;
    }

    public boolean isValid() {
        if (mModeCopy == mModeMove) {
            addValidationError("Pick one operation of cp/mv");
        } else {
            mCommand = mModeCopy ? Command.COPY : Command.MOVE;
        }

        try {
            mPathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + mFilePattern);
        } catch (Exception e) {
            addValidationError("invalid file pattern: " + mFilePattern);
        }

        try {
            mDateFormat = new SimpleDateFormat(mDatePattern);
        } catch (Exception e) {
            addValidationError("invalid date pattern: " + mDatePattern);
        }

        try {
            mDateSource = DateSource.valueOf(mDateSourceString.toUpperCase());
        } catch (Exception e) {
            addValidationError("invalid date source: " + mDateSourceString);
        }

        if (mSourceDir == null || !mSourceDir.isDirectory()) {
            addValidationError("invalid source directory: " + mSourceDir);
        }

        if (mDestDir == null || !mDestDir.isDirectory()) {
            addValidationError("invalid dest directory: " + mDestDir);
        }

        return mValidationErrorBuilder.length() == 0;
    }

    public void setCommand(Command operationMode) {
        mCommand = operationMode;
    }

    public void setDatePattern(String datePattern) {
        mDatePattern = datePattern;
    }

    public void setDateSource(DateSource dateSource) {
        mDateSource = dateSource;
    }

    public void setDateSourceString(String dateSourceString) {
        mDateSourceString = dateSourceString;
    }

    public void setDestDir(File dest) {
        mDestDir = dest;
    }

    public void setDryRun(boolean dryRun) {
        mDryRun = dryRun;
    }

    public void setFilePattern(String filePattern) {
        mFilePattern = filePattern;
    }

    public void setFollowLinks(boolean links) {
        mFollowLinks = links;
    }

    public void setModeCopy(boolean modeCopy) {
        mModeCopy = modeCopy;
    }

    public void setModeMove(boolean modeMove) {
        mModeMove = modeMove;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        mPathMatcher = pathMatcher;
    }

    public void setRecursive(boolean recursive) {
        mRecursive = recursive;
    }

    public void setSourceAndDest(String[] args) {
        if (args.length == 2) {
            String source = args[0];
            File sourceFile = new File(source);

            if (sourceFile.isDirectory()) {
                mSourceDir = sourceFile;
            } else {
                String sourceDir = FilenameUtils.getFullPathNoEndSeparator(source);
                mSourceDir = new File(sourceDir);
                mFilePattern = FilenameUtils.getName(source);
            }

            setDestDir(new File(args[1]));
        } else {
            addValidationError("invalid arg count");
        }
    }

    public void setSourceDir(File source) {
        mSourceDir = source;
    }

    @Override
    public String toString() {
        return "OptionsHolder {"
                + "\n OperationMode=" + mCommand
                + "\n"
                + "\n DateSource=" + mDateSource
                + "\n DatePattern=" + mDatePattern
                + "\n FilePattern=" + mFilePattern
                + "\n"
                + "\n DryRun=" + mDryRun
                + "\n Links=" + mFollowLinks
                + "\n Recursive=" + mRecursive
                + "\n"
                + "\n Source=" + mSourceDir
                + "\n Dest=" + mDestDir
                + "\n}";
    }

    private void addValidationError(String string) {
        mValidationErrorBuilder.append(string).append("\n");
    }
}