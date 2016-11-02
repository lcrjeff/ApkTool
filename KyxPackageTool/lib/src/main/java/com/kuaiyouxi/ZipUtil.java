package com.kuaiyouxi;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	private ZipOutputStream out;
	private String path;

	public void zip(File temp, String zipFileStr) {

		try {
			if (!temp.exists()) {
				return;
			}
			File zipFile = new File(zipFileStr);
			zipFile.getParentFile().mkdirs();
			out = new ZipOutputStream(new FileOutputStream(zipFileStr));
			if (out == null) {
				return;
			}
			path = temp.getParent();
			a(temp);
			out.close();
		} catch (Exception e) {
		}
	}

	public void a(File temp) {
		if (temp.isDirectory()) {
			for (File f : temp.listFiles()) {
				a(f);
			}
		} else {
			temp.getAbsolutePath();
			try {
				out.putNextEntry(new ZipEntry(temp.getAbsolutePath().substring(
						path.length() + 1)));
				FileInputStream in = new FileInputStream(temp);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static void UnZip(final File temp, final String outPath) {
		Thread unZip = new Thread() {
			public void run() {
				
			}
		};
		unZip.start();
		try {
			String zipFileString = temp.getPath();
			ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
			ZipEntry zipEntry;
			String szName = "";
			long length = 0;
			while ((zipEntry = inZip.getNextEntry()) != null) {
				szName = zipEntry.getName();
				if (zipEntry.isDirectory()) {
					// get the folder name of the widget
					szName = szName.substring(0, szName.length() - 1);
					File folder = new File(outPath
							+ File.separator + szName);
					folder.mkdirs();
				} else {
					File file = new File(outPath + File.separator
							+ szName);
					if (file.exists()) {
						// if(!file.getName().endsWith("bat"))
						continue;
						// else
						// file.delete();
					}
					FileUtil.createNewFile(file);
					// file.createNewFile();
					// get the output stream of the file
					BufferedOutputStream bos = new BufferedOutputStream(
							new FileOutputStream(file), 1024);
					int len;
					byte[] buffer = new byte[1024];
					// read (len) bytes into buffer
					while ((len = inZip.read(buffer)) != -1) {

						bos.write(buffer, 0, len);
						bos.flush();
						length += len;
					}
					bos.close();
				}
			}// end of while
			inZip.close();
			length = 0;
			// 11.25
			temp.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void addFileToZip2(File zipFile, String dir,File fileToAdd) throws IOException {  
        Map<String, String> env = new HashMap<String, String>();  
        env.put("create", "true");  
        try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + zipFile.toURI()), env)) {
            Path pathToAddFile = fileToAdd.toPath();
            Path pathInZipfile = null;
            if(dir == null || "".equals(dir)){
            	pathInZipfile = fs.getPath("/" + fileToAdd.getName());  
            }else{
            	pathInZipfile = fs.getPath(dir+"/" + fileToAdd.getName());  
            }
            Files.copy(pathToAddFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
        }  
    }
}
