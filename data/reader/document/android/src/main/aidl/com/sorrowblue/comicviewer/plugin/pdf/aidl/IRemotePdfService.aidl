package com.sorrowblue.comicviewer.plugin.pdf.aidl;

import com.sorrowblue.comicviewer.plugin.aidl.CompressFormat;
import com.sorrowblue.comicviewer.plugin.aidl.FileReader;
import com.sorrowblue.comicviewer.plugin.aidl.ISeekableInputStream;

/**
 * Remote service interface for PDF operations.
 * Provides a method to obtain a FileReader from an input stream and magic string.
 */
interface IRemotePdfService {

    /**
     * Returns a FileReader instance for the given input stream and magic string.
     *
     * @param inputStream Input stream to read PDF data
     * @param magic Magic string for identification
     * @return FileReader instance
     */
    FileReader getFIleReader(ISeekableInputStream inputStream, String magic);

    void setCompressFormat(CompressFormat compressFormat);
    CompressFormat getCompressFormat();

    void setQuality(int quality);
    int getQuality();

    String getVersion();
}
