package com.sorrowblue.comicviewer.pdf.aidl;

import com.sorrowblue.comicviewer.pdf.aidl.IOutputStream;

interface FileReader {
    int pageCount();
    String loadPage(int pageIndex);
    long fileSize(int pageIndex);

    void close();
}
