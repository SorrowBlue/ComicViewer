package com.sorrowblue.comicviewer.pdf.aidl;

interface IOutputStream {
    void write(int b);
    void flush();
    void close();
}
