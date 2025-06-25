package com.sorrowblue.comicviewer.pdf.aidl;

interface IOutputStream {
    void write(int b);
    void write2(inout byte[] b);
    void write3(inout byte[] b, int off, int len);
    void flush();
    void close();
}
