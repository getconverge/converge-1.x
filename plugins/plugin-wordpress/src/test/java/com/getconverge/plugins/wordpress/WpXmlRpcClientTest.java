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
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link WpXmlRpcClient}. For the unit tests to pass the
 * following conditions must be met: <ul>
 * <li>A post with id = 1 must exist</li>
 * <li>A user with id = 2 must exist</li>
 * </ul>
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
        final Integer nonexisting = 9999999;
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
        Map<String, Object> post = null;
        final String POST_TITLE = "TEST wpXmlRpcClient_createNewPost_postCreated";
        final String POST_CONTENT = "The post content";
        final String POST_EXCERPT = "The post brief";
        final Integer POST_AUTHOR = 2;
        final String[] POST_TAXONOMY_POST_TAGS = new String[]{"test", "converge", "plugin"};
        final String[] POST_TAXONOMY_POST_CATEGORIES = new String[]{"Agile Development"};
        final int TERM_COUNT = POST_TAXONOMY_POST_TAGS.length + POST_TAXONOMY_POST_CATEGORIES.length;

        // Act
        try {
            postId = client.createPost("post", PostStatus.PUBLISH, POST_AUTHOR, POST_TITLE, POST_CONTENT, POST_EXCERPT, POST_TAXONOMY_POST_TAGS, POST_TAXONOMY_POST_CATEGORIES);
            exist = client.exists(postId);
            post = client.getPost(postId);
        } catch (WpXmlRpcClientException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertNotNull(postId);
        assertNotNull(post);
        assertTrue(exist);
        assertEquals(POST_TITLE, post.get(PostField.POST_TITLE.toString()));
        assertEquals(POST_CONTENT, post.get(PostField.POST_CONTENT.toString()));
        assertEquals(POST_EXCERPT, post.get(PostField.POST_EXCERPT.toString()));
        assertEquals(POST_AUTHOR, Integer.valueOf((String) post.get(PostField.POST_AUTHOR.toString())));
        assertEquals(TERM_COUNT, ((Object[]) post.get(PostField.TERMS.toString())).length);
    }

    @Test
    public void wpXmlRpcClient_createNewPostDeletePost_postDeleted() {
        // Arrange
        WpXmlRpcClient client = new WpXmlRpcClient(WORDPRESS_URL, WORDPRESS_UID, WORDPRESS_PWD);
        boolean deleted = false;
        try {
            // Act
            Integer postId = client.createPost("post", PostStatus.PUBLISH, 2, "TEST wpXmlRpcClient_createNewPostDeletePost_postDeleted", "the content", "brief", new String[]{"test", "converge", "plugin"}, new String[]{"Integration Testing"});
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
        Map<String, Object> uploadedFile = null;
        final String FILE_UPLOAD_NAME = "test-image.png";
        final String FILE_UPLOAD_TYPE = "image/png";

        try {
            // Act
            Integer postId = client.createPost("post", PostStatus.PUBLISH, 2, "TEST wpXmlRpcClient_createNewPostUploadFile_postCreatedWithFile", "This is the <b>content</b> of a post with a <u>media file</u>", "brief", new String[]{"test", "converge", "file attachment"}, new String[]{"Integration Testing"});
            uploadedFile = client.uploadFile(FILE_UPLOAD_NAME, FILE_UPLOAD_TYPE, bits, true, postId);
        } catch (WpXmlRpcClientException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertNotNull(uploadedFile);
        assertTrue(NumberUtils.isNumber((String) uploadedFile.get(FileField.ID.toString())));
        assertEquals("wpid-" + FILE_UPLOAD_NAME, uploadedFile.get(FileField.FILE.toString()));
        assertEquals(FILE_UPLOAD_TYPE, uploadedFile.get(FileField.TYPE.toString()));
    }

    @Test
    public void wpXmlRpcClient_createNewPostEditPost_postCreatedAndUpdated() throws IOException {
        // Arrange
        WpXmlRpcClient client = new WpXmlRpcClient(WORDPRESS_URL, WORDPRESS_UID, WORDPRESS_PWD);
        Map<String, Object> updatedPost = null;

        final String POST_TITLE_UPDATED = "TEST wpXmlRpcClient_createNewPostEditPost_postCreatedAndUpdated 2";
        final String POST_CONTENT_UPDATED = "This is the <b>updated content</b> of a post";
        final String POST_EXCERPT_UPDATED = "Updated brief";
        final String[] POST_TAGS_UPDATED = new String[]{"test", "converge", "second"};
        final String[] POST_CATEGORY_UPDATED = new String[]{"Integration Testing"};
        final int POST_TERMS_COUNT = POST_TAGS_UPDATED.length + POST_CATEGORY_UPDATED.length;

        try {
            // Act
            Integer postId = client.createPost("post", PostStatus.PUBLISH, 2, "TEST wpXmlRpcClient_createNewPostEditPost_postCreatedAndUpdated", "This is the <b>content</b> of a post", "brief", new String[]{"test", "converge", "first"}, new String[]{"Integration Testing"});
            client.editPost(postId, "post", PostStatus.PUBLISH, 2, POST_TITLE_UPDATED, POST_CONTENT_UPDATED, POST_EXCERPT_UPDATED, POST_TAGS_UPDATED, POST_CATEGORY_UPDATED);
            updatedPost = client.getPost(postId);
        } catch (WpXmlRpcClientException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertNotNull(updatedPost);
        assertEquals(POST_TITLE_UPDATED, updatedPost.get(PostField.POST_TITLE.toString()));
        assertEquals(POST_CONTENT_UPDATED, updatedPost.get(PostField.POST_CONTENT.toString()));
        assertEquals(POST_EXCERPT_UPDATED, updatedPost.get(PostField.POST_EXCERPT.toString()));
        assertEquals(POST_TERMS_COUNT, ((Object[]) updatedPost.get(PostField.TERMS.toString())).length);
    }

    // -- Helper Functions -----------------------------------------------------
    private void outputPost(Map<String, Object> post) {
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
