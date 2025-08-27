package com.sorrowblue.comicviewer.plugin.aidl;

import com.sorrowblue.comicviewer.plugin.aidl.CompressFormat;

/**
 * Interface for reading PDF files, providing page count, page loading, file size, and closing operations.
 */
interface FileReader {

    /**
     * Returns the total number of pages in the PDF file.
     *
     * @return Total page count
     */
    int pageCount();

    /**
     * Loads the specified page and returns its content as a String.
     *
     * @param pageIndex Index of the page to load
     * @return Page content as String
     */
    String loadPage(int pageIndex);

    /**
     * Loads the specified page and returns its content as a String.
     *
     * @param pageIndex Index of the page to load
     * @return Page content as String
     */
    String loadPageWithFortmat(int pageIndex, CompressFormat format, int quality);

    /**
     * Returns the file size for the specified page.
     *
     * @param pageIndex Index of the page
     * @return File size in bytes
     */
    long fileSize(int pageIndex);

    /**
     * Closes the file reader and releases resources.
     */
    void close();
}
