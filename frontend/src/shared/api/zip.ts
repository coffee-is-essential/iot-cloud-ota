import JSZip from "jszip";

/**
 * Compresses a file using JSZip and returns a new File object.
 * @param {File} file - The file to be compressed.
 * @returns {Promise<File>} - A promise that resolves to the compressed file.
 */
export const zipFile = async (file: File): Promise<File> => {
  const zip = new JSZip();

  zip.file(file.name, file);

  const zipContent = await zip.generateAsync({
    type: "blob",
    compression: "DEFLATE",
    compressionOptions: {
      level: 9,
    },
  });

  const compressedFile = new File([zipContent], `${file.name}.zip`, {
    type: "application/zip",
    lastModified: Date.now(),
  });

  return compressedFile;
};
