package com.sorrowblue.comicviewer.pdf.aidl;

import com.sorrowblue.comicviewer.pdf.aidl.ISeekableInputStream;
import com.sorrowblue.comicviewer.pdf.aidl.FileReader;

interface IRemotePdfService {

    FileReader getFIleReader(ISeekableInputStream inputStream, String magic);
}

