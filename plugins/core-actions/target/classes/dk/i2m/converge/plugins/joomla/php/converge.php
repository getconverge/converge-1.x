<?php

/**
 * @package Converge API for Joomla! 1.5.x
 * @version 1.0.9
 * @copyright Copyright(c) 2010 - 2011 Interactive Media Management All Rights Reserved.
 * @license http://www.gnu.org/licenses/gpl-3.0.html GNU/GPL
 */
// no direct access
defined('_JEXEC') or die('Restricted access');

jimport('joomla.plugin.plugin');

define("CONVERGE_VERSION", "1.0.9");

class plgXMLRPCConverge extends JPlugin {

    function plgXMLRPCConverge(&$subject, $config) {
        parent::__construct($subject, $config);
    }

    function onGetWebServices() {
        return array(
            'converge.listCategories' => array(
                'function' => 'plgXMLRPCConvergeServices::listCategories',
                'signature' => null),
            'converge.newArticle' => array(
                'function' => 'plgXMLRPCConvergeServices::newArticle',
                'signature' => null),
            'converge.editArticle' => array(
                'function' => 'plgXMLRPCConvergeServices::editArticle',
                'signature' => null),
            'converge.deleteArticle' => array(
                'function' => 'plgXMLRPCConvergeServices::deleteArticle',
                'signature' => null),
            'converge.newMedia' => array(
                'function' => 'plgXMLRPCConvergeServices::newMedia',
                'signature' => null),
            'converge.version' => array(
                'function' => 'plgXMLRPCConvergeServices::version',
                'signature' => null)
        );
    }

}

class plgXMLRPCConvergeServices {

    function version() {
        return (new xmlrpcresp(new xmlrpcval(CONVERGE_VERSION, "string")));
    }

    /**
     * Returns all content categories from the Joomla installation.
     *
     * @global type $xmlrpcerruser User error indicator constant
     * @global type $xmlrpcArray XML RPC array type
     * @return xmlrpcresp XML RPC response containing a list of all content categories
     */
    function listCategories() {
        global $xmlrpcerruser, $xmlrpcArray;

        // Get arguments passed to the function
        $args = func_get_args();

        // Checks the number of arguments
        if (func_num_args() != 2) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Invalid request - must have 2 arguments. Request had " . func_num_args() . " arguments");
        }

        // Read the arguments into local variables
        $username = $args[0];
        $password = $args[1];

        // Authenticate user
        $user = plgXMLRPCConvergeHelper::authenticateUser($username, $password);

        if (!$user) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Authentication failed.");
        }

        $cats = plgXMLRPCConvergeHelper::getJoomlaCategories();

        $structArray = array();
        foreach ($cats as $cat) {
            $tst = print_r($cat, true);
            $structArray[] = new xmlrpcval(array(
                        'id' => new xmlrpcval($cat->id),
                        'title' => new xmlrpcval($cat->title)), 'struct');
        }

        return new xmlrpcresp(new xmlrpcval($structArray, $xmlrpcArray));
    }

    function newArticle() {
        global $xmlrpcerruser;

        // Get arguments passed to the function
        $args = func_get_args();

        // Checks the number of arguments
        if (func_num_args() != 14) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Invalid request - must have 14 arguments. Request had " . func_num_args() . " arguments");
        }

        // Read the arguments into local variables
        $username = $args[0];
        $password = $args[1];
        $convergeId = $args[2];
        $title = $args[3];
        $brief = $args[4];
        $content = $args[5];
        $author = $args[6];
        $categoryId = $args[7];
        $frontpage = $args[8];
        $priority = $args[9];
        $keywords = $args[10];
        $description = $args[11];
        $publish = $args[12];
        $expiration = $args[13];

        // Authenticate user
        $user = plgXMLRPCConvergeHelper::authenticateUser($username, $password);

        if (!$user) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Authentication failed.");
        }

        $existingId = plgXMLRPCConvergeHelper::getJoomlaMapping($convergeId);

        if ($existingId == 0) {
            // Construct article
            $article = & JTable::getInstance('content');
            $article->title = $title;

            $article->catid = (int) $categoryId;
            if ($categoryId == '0') {
                $article->sectionid = 0;
            } else {
                $article->sectionid = plgXMLRPCConvergeHelper::getJoomlaSectionByCategory($article->catid);
            }
            $article->created_by = $user->id;

            $article->introtext = $brief;
            if ($content == '') {
                $article->fulltext = $brief;
            } else {
                $article->fulltext = $content;
            }
            $createdDate = JFactory::getDate();
            $article->created = $createdDate->toMySQL();

            if ($publish != '0') {
                $publishDate = JFactory::getDate(iso8601_decode($publish));
                $article->publish_up = $publishDate->toMySQL();
                $article->created = $publishDate->toMySQL();
            } else {
                $article->publish_up = $createdDate->toMySQL();
            }

            if ($expiration != '0') {
                $expirationDate = JFactory::getDate(iso8601_decode($expiration));
                $article->publish_down = $expirationDate->toMySQL();
            } else {
                $article->publish_down = '0000-00-00 00:00:00';
            }

            $article->metakey = $keywords;
            $article->metadesc = $description;
            $article->created_by_alias = $author;
            $article->state = 1;
            $article->ordering = $priority;

            if (!$article->check()) {
                return new xmlrpcresp(0, $xmlrpcerruser + 3, "Post integrity check failed: " . $article->getError());
            }

            $article->version++;

            if (!$article->store()) {
                return new xmlrpcresp(0, $xmlrpcerruser + 4, "Posting failed: " . $article->getError());
            }

            //clear cache
            $cache = & JFactory::getCache('com_content');
            $cache->clean();

            plgXMLRPCConvergeHelper::createMapping($convergeId, $article->id);


            if ($frontpage == 'true') {
                plgXMLRPCConvergeHelper::addToFrontpage($article->id, $priority);
            }

            return (new xmlrpcresp(new xmlrpcval($article->id, "string")));
        } else {
            return plgXMLRPCConvergeServices::editArticle($username, $password, $convergeId, $existingId, $title, $brief, $content, $author, $categoryId, $frontpage, $priority, $keywords, $description, $publish, $expiration);
        }
    }

    function editArticle() {
        global $xmlrpcerruser;

        // Get arguments passed to the function
        $args = func_get_args();

        // Checks the number of arguments
        if (func_num_args() != 15) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Invalid request - must have 15 arguments. Request had " . func_num_args() . " arguments");
        }

        // Read the arguments into local variables
        $username = $args[0];
        $password = $args[1];
        $convergeId = $args[2];
        $articleId = $args[3];
        $title = $args[4];
        $brief = $args[5];
        $content = $args[6];
        $author = $args[7];
        $categoryId = $args[8];
        $frontpage = $args[9];
        $priority = $args[10];
        $keywords = $args[11];
        $description = $args[12];
        $publish = $args[13];
        $expiration = $args[14];

        // Authenticate user
        $user = plgXMLRPCConvergeHelper::authenticateUser($username, $password);

        if (!$user) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Authentication failed.");
        }

        // Construct article
        $article = & JTable::getInstance('content');
        if (!$article->load((int) $articleId)) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, 'Article #' . $articleId . ' not found.');
        }
        $article->checkout((int) $user->id);
        $article->title = $title;
        $article->catid = (int) $categoryId;
        if ($categoryId == '0') {
            $article->sectionid = 0;
        } else {
            $article->sectionid = plgXMLRPCConvergeHelper::getJoomlaSectionByCategory($article->catid);
        }
        $article->created_by = $user->id;

        $article->introtext = $brief;
        if ($content == '') {
            $article->fulltext = $brief;
        } else {
            $article->fulltext = $content;
        }

        if ($publish != '0') {
            $publishDate = JFactory::getDate(iso8601_decode($publish));
            $article->publish_up = $publishDate->toMySQL();
        } else {
            $nowDate = JFactory::getDate();
            $article->publish_up = $nowDate->toMySQL();
        }

        if ($expiration != '0') {
            $expirationDate = JFactory::getDate(iso8601_decode($expiration));
            $article->publish_down = $expirationDate->toMySQL();
        } else {
            $article->publish_down = '0000-00-00 00:00:00';
        }

        $article->metakey = $keywords;
        $article->metadesc = $description;
        $article->created_by_alias = $author;
        $article->state = 1;
        $article->ordering = $priority;

        if ($frontpage == 'true') {
            plgXMLRPCConvergeHelper::addToFrontpage($article->id, $priority);
        }

        if (!$article->check()) {
            return new xmlrpcresp(0, $xmlrpcerruser + 3, "Post integrity check failed: " . $article->getError());
        }

        $article->version++;

        if (!$article->store()) {
            return new xmlrpcresp(0, $xmlrpcerruser + 4, "Posting failed: " . $article->getError());
        }

        $article->checkin();

        //clear cache
        $cache = & JFactory::getCache('com_content');
        $cache->clean();

        return (new xmlrpcresp(new xmlrpcval($article->id, "string")));
    }

    function deleteArticle() {
        global $xmlrpcerruser;

        // Get arguments passed to the function
        $args = func_get_args();

        // Checks the number of arguments
        if (func_num_args() != 3) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Invalid request - must have 3 arguments. Request had " . func_num_args() . " arguments");
        }

        // Read the arguments into local variables
        $username = $args[0];
        $password = $args[1];
        $convergeId = $args[2];

        // Authenticate user
        $user = plgXMLRPCConvergeHelper::authenticateUser($username, $password);

        if (!$user) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Authentication failed.");
        }

        $articleId = plgXMLRPCConvergeHelper::getJoomlaMapping($convergeId);

        // Construct article
        $article = & JTable::getInstance('content');
        if (!$article->load((int) $articleId)) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, 'Article #' . $articleId . ' not found.');
        }
        $article->checkout((int) $user->id);

        $article->state = -2;
        $article->ordering = 0;

        if (!$article->store()) {
            return new xmlrpcresp(0, $xmlrpcerruser + 4, "Posting failed: " . $article->getError());
        }

        $article->checkin();

        plgXMLRPCConvergeHelper::removeMapping($convergeId);

        //clear cache
        $cache = & JFactory::getCache('com_content');
        $cache->clean();

        return (new xmlrpcresp(new xmlrpcval($article->id, "string")));
    }

    function newMedia() {
        global $xmlrpcerruser;

        // Get arguments passed to the function
        $args = func_get_args();

        // Checks the number of arguments
        $argCount = func_num_args();
        if ($argCount != 5) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Illegal request - got " . $argCount . " arguments");
        }

        // Load plug-in settings
        $pluginSettings = plgXMLRPCConvergeHelper::getPluginSettings();

        // Read the arguments into local variables
        $username = $args[0];
        $password = $args[1];
        $articleId = $args[2];
        $file_name = $args[3];
        $file = $args[4];

        $sub_path = $pluginSettings->img_storage_path . DS . $articleId . DS;

        $images_path = JPATH_ROOT . DS . $sub_path;

        if (!file_exists($images_path)) {
            mkdir($images_path);
        }

        if (empty($file)) {
            return new xmlrpcresp(0, $xmlrpcerruser + 4, 'File is empty');
        }

        // Create file pointer
        $fp = fopen($images_path . $file_name, 'wb');
        if (!$fp) {
            return new xmlrpcresp(0, $xmlrpcerruser + 5, "Can't create : $file_name");
        }

        if (fputs($fp, $file) === false) {
            return new xmlrpcresp(0, $xmlrpcerruser + 6, "Can't write : $file_name");
        }
        fclose($fp);

        if (!file_exists($images_path . $file_name)) {
            return new xmlrpcresp(0, $xmlrpcerruser + 7, "Can't create : $images_path$file_name");
        }

        // Authenticate user
        $user = plgXMLRPCConvergeHelper::authenticateUser($username, $password);

        if (!$user) {
            return new xmlrpcresp(0, $xmlrpcerruser + 1, "Authentication failed.");
        }

        return (new xmlrpcresp(new xmlrpcval($sub_path . $file_name, "string")));
    }

}

class plgXMLRPCConvergeHelper {

    /**
     * Authenticates a user against the Joomla user database.
     *
     * @param string $username Username of the user
     * @param string $password Password of the user
     * @return boolean true if the user exist and belongs to the Publisher group, otherwise false
     */
    function authenticateUser($username, $password) {
        jimport('joomla.user.authentication');
        $auth = & JAuthentication::getInstance();
        $credentials['username'] = $username;
        $credentials['password'] = $password;
        $authuser = $auth->authenticate($credentials, null);

        // Ensure that the user is registered
        if ($authuser->status == JAUTHENTICATE_STATUS_FAILURE || empty($authuser->username) || empty($authuser->password) || empty($authuser->email)) {
            return false;
        }

        // Get the user record from the database
        $user = & JUser::getInstance($authuser->username);

        // Ensure that the user is activated and not blocked
        if (empty($user->id) || $user->block || !empty($user->activation)) {
            return false;
        }

        // Check user type - only the Publisher type is allowed access
        $type = $user->usertype;
        $gid = (int) $user->gid;

        if (strcmp($type, 'Publisher') === 0 && $gid === 21) {
            return $user;
        } else {
            return false;
        }
    }

    function getPluginSettings() {
        $plugin = & JPluginHelper::getPlugin('xmlrpc', 'converge');

        $params = new JParameter($plugin->params);

        $temp = new stdClass();

        $temp->db_enc = $params->get('db_enc', 'UTF-8');
        $temp->enc = $GLOBALS['mainframe']->getEncoding();

        $temp->img_storage_path = $params->get('img_storage_path', 'images/stories/');

        return $temp;
    }

    function getJoomlaSectionByCategory($catid) {
        $db = & JFactory::getDBO();
        $query = "SELECT section FROM #__categories WHERE id = " . (int) $catid;
        $db->setQuery($query);
        $db->query();
        $res = $db->loadResult();
        return (is_numeric($res) ? (int) $res : 0);
    }

    /**
     * Gets an array of content categories. Note that
     * non content categories (such as categories for
     * banners, contacts, and weblinks) are not
     * included.
     *
     * @return array of categories 
     */
    function getJoomlaCategories() {
        $db = & JFactory::getDBO();
        $query = "SELECT * FROM #__categories WHERE section REGEXP ('[0-9]')";
        $db->setQuery($query);
        $db->query();
        return $db->loadObjectList();
    }

    /**
     * Gets the Joomla Article ID of a given Converge News Item ID.
     *
     * @param int $convergeId ID of the Converge News Item
     * @return int Unique identifier of the article matching the given Converge ID. 0 is returned if no match.
     */
    function getArticleIdFromConvergeId($convergeId) {
        $db = & JFactory::getDBO();
        $query = "SELECT article_id FROM #__converge_mapping WHERE converge_id = " . (int) $convergeId;
        $db->setQuery($query);
        $db->query();
        $res = $db->loadResult();
        return (is_numeric($res) ? (int) $res : 0);
    }

    function createMapping($convergeId, $articleId) {
        $db = & JFactory::getDBO();
        $query = "INSERT INTO #__converge_mapping (article_id, converge_id) VALUES (" . ((int) $articleId) . ", " . ((int) $convergeId) . ");";
        $db->setQuery($query);
        $db->query();
    }

    function removeMapping($convergeId) {
        $db = & JFactory::getDBO();
        $query = "DELETE FROM #__converge_mapping WHERE converge_id = " . ((int) $convergeId) . ";";
        $db->setQuery($query);
        $db->query();
    }

    function addToFrontpage($articleId, $priority) {
        $db = & JFactory::getDBO();
        $query = "INSERT INTO #__content_frontpage (content_id, ordering) VALUES (" . ((int) $articleId) . ", " . ((int) $priority) . ");";
        $db->setQuery($query);
        $db->query();
    }

    function getJoomlaMapping($convergeId) {
        $db = & JFactory::getDBO();
        $query = "SELECT article_id FROM #__converge_mapping WHERE converge_id=" . ((int) $convergeId) . ";";
        $db->setQuery($query);
        $db->query();
        $res = $db->loadResult();
        return (is_numeric($res) ? (int) $res : 0);
    }

}
