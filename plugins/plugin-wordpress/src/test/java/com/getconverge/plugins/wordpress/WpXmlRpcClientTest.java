/*
 * Copyright (C) 2015 Allan Lykke Christensen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.getconverge.plugins.wordpress;

import dk.i2m.commons.FileUtils;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link WpXmlRpcClient}.
 *
 * @author Allan Lykke Christensen
 */
public class WpXmlRpcClientTest {

    // TODO: Replace local instance with online Integration Testing instance
    private static final String WORDPRESS_URL = "http://localhost/int.wordpress.getconverge.com";
    private static final String WORDPRESS_UID = "admin";
    private static final String WORDPRESS_PWD = "admin";

    @Test
    public void wpXmlRpcClient_requestNonexistingPost_returnFalse() {
        // Arrange
        WpXmlRpcClient client = new WpXmlRpcClient(WORDPRESS_URL, WORDPRESS_UID, WORDPRESS_PWD);
        Integer nonexisting = 9999999;
        boolean exist = false;
        try {
            // Act
            exist = client.exists(nonexisting);
        } catch (WpXmlRpcClientException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertFalse(exist);
    }

    @Test
    public void wpXmlRpcClient_requestExistingPost_returnTrue() {
        // Arrange
        WpXmlRpcClient client = new WpXmlRpcClient(WORDPRESS_URL, WORDPRESS_UID, WORDPRESS_PWD);
        Integer existing = 1;
        boolean exist = false;
        try {
            // Act
            exist = client.exists(existing);
        } catch (WpXmlRpcClientException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertTrue(exist);
    }

    @Test
    public void wpXmlRpcClient_createNewPost_postCreated() {
        // Arrange
        WpXmlRpcClient client = new WpXmlRpcClient(WORDPRESS_URL, WORDPRESS_UID, WORDPRESS_PWD);
        Integer postId = null;
        boolean exist = false;
        try {
            // Act
            postId = client.createPost("post", PostStatus.PUBLISH, "just testing", "the content", "brief", new String[]{"test", "converge", "plugin"}, new String[]{"Agile Development"});
            exist = client.exists(postId);
        } catch (WpXmlRpcClientException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertNotNull(postId);
        assertTrue(exist);
    }

    @Test
    public void wpXmlRpcClient_createNewPostDeletePost_postDeleted() {
        // Arrange
        WpXmlRpcClient client = new WpXmlRpcClient(WORDPRESS_URL, WORDPRESS_UID, WORDPRESS_PWD);
        boolean deleted = false;
        try {
            // Act
            Integer postId = client.createPost("post", PostStatus.PUBLISH, "TEST wpXmlRpcClient_createNewPostDeletePost_postDeleted", "the content", "brief", new String[]{"test", "converge", "plugin"}, new String[]{"Agile Development"});
            deleted = client.deletePost(postId);
        } catch (WpXmlRpcClientException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertTrue("Post wasn't deleted", deleted);
    }

    @Test
    public void wpXmlRpcClient_createNewPostUploadFile_postCreatedWithFile() throws IOException {
        // Arrange
        WpXmlRpcClient client = new WpXmlRpcClient(WORDPRESS_URL, WORDPRESS_UID, WORDPRESS_PWD);
        byte[] bits = FileUtils.getBytes(getClass().getResourceAsStream("/com/getconverge/plugins/wordpress/test-image-upload.png"));

        try {
            // Act
            Integer postId = client.createPost("post", PostStatus.PUBLISH, "TEST wpXmlRpcClient_createNewPostUploadFile_postCreatedWithFile", "This is the <b>content</b> of a post with a <u>media file</u>", "brief", new String[]{"test", "converge", "file attachment"}, new String[]{"Integration Testing"});
            Map<String, Object> file = client.uploadFile("test-image.png", "image/png", bits, true, postId);
            Map<String, Object> currentPost = client.getPost(postId);
            String imgTag = "<img class=\"size-full wp-image-" + file.get("id") + "\" src=\"" + ((String) file.get("url")) + "\" />";
            client.editPost(postId, "post", PostStatus.PUBLISH, (String) currentPost.get("post_title"), imgTag + (String) currentPost.get("post_content"), (String) currentPost.get("post_excerpt"), new String[]{"second"}, new String[]{"Integration Testing"});
            //Map<String, Object> mediaItem = client.getMediaFile(1, Integer.valueOf((String)file.get("id")));
        } catch (WpXmlRpcClientException ex) {
            fail(ex.getMessage());
        }

        // Assert
        // TODO: Asserts
    }

    @Test
    public void wpXmlRpcClient_createNewPostEditPost_postCreatedAndUpdated() throws IOException {
        // Arrange
        WpXmlRpcClient client = new WpXmlRpcClient(WORDPRESS_URL, WORDPRESS_UID, WORDPRESS_PWD);

        try {
            // Act
            Integer postId = client.createPost("post", PostStatus.PUBLISH, "TEST wpXmlRpcClient_createNewPostEditPost_postCreatedAndUpdated", "This is the <b>content</b> of a post", "brief", new String[]{"test", "converge", "first"}, new String[]{"Integration Testing"});
            client.editPost(postId, "post", PostStatus.PUBLISH, "TEST wpXmlRpcClient_createNewPostEditPost_postCreatedAndUpdated 2", "This is the <b>updated content</b> of a post", "brief", new String[]{"test", "converge", "second"}, new String[]{"Integration Testing"});
        } catch (WpXmlRpcClientException ex) {
            fail(ex.getMessage());
        }

        // Assert
        // TODO: Asserts
    }

    private void outputPost(Map<String, Object[]> post) {
        System.out.println("========================");
        for (String postKey : post.keySet()) {
            Object val = post.get(postKey);
            System.out.println("Key: " + postKey + " Value: " + val + " [" + val.getClass() + "]");

            if (val instanceof Object[]) {
                System.out.println("Size: " + ((Object[]) val).length);
                for (Object obj : (Object[]) val) {
                    System.out.println("- Subvalue: " + obj + " type: " + obj.getClass());
                }
            }
        }
    }

}
