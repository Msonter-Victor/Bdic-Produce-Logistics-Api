/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cloudinary.Uploader
 *  com.cloudinary.utils.ObjectUtils
 *  dev.gagnon.exception.UploadMediaFailedException
 *  dev.gagnon.utils.ServiceUtils
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.multipart.MultipartFile
 */
package dev.gagnon.utils;

import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import dev.gagnon.exception.UploadMediaFailedException;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public class ServiceUtils {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);

    public static String getMediaUrl(MultipartFile mediaFile, Uploader uploader) {
        log.info("Trying to upload image to Cloudinary");
        try {
            Map map = ObjectUtils.asMap((Object[])new Object[]{"resource_type", "image", "use_filename", true});
            Map uploadResponse = uploader.upload((Object)mediaFile.getBytes(), map);
            log.info("Image uploaded successfully");
            return uploadResponse.get("url").toString();
        }
        catch (Exception exception) {
            log.error("Error while uploading image to Cloudinary", (Throwable)exception);
            throw new UploadMediaFailedException("a: " + exception.getMessage());
        }
    }
}

