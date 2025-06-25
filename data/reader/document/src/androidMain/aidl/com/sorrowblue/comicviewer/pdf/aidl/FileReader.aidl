package com.sorrowblue.comicviewer.pdf.aidl;

import com.sorrowblue.comicviewer.pdf.aidl.IOutputStream;

interface FileReader {
    int pageCount();
    void copyTo(int pageIndex, IOutputStream stream);
    long fileSize(int pageIndex);

    void close();
}
