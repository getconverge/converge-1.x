/**
 * editor_plugin_src.js
 *
 * Copyright 2009, Moxiecode Systems AB
 * Released under LGPL License.
 *
 * License: http://tinymce.moxiecode.com/license
 * Contributing: http://tinymce.moxiecode.com/contributing
 */

(function() {
    tinymce.create('tinymce.plugins.WordCount', {
        block : 0,
        id : null,
        elem : null,
        countre : null,
        cleanre : null,
        recalc_keys : [],
        limit : -1,
        limit_class : null,

        init : function(ed, url) {
            var t = this, last = 0;

            t.countre = ed.getParam('wordcount_countregex', /\S\s+/g);
            t.cleanre = ed.getParam('wordcount_cleanregex', /[0-9.(),;:!?%#$À'"_+=\\/-]*/g);
            t.recalc_keys = ed.getParam('wordcount_recalc_keys', [13,32,190,8,46]);
            t.block_ms = ed.getParam('wordcount_block_ms', 200);
            t.limit = ed.getParam('wordcount_limit', -1);
            t.limit_class = ed.getParam('wordcount_limit_class', 'wordcount_limit');
            t.id = ed.id + '-word-count';

            ed.onPostRender.add(function(ed, cm) {
                var row, id;

                // Add it to the specified id or the theme advanced path
                id = ed.getParam('wordcount_target_id');
                if (!id) {
                    row = tinymce.DOM.get(ed.id + '_path_row');

                    if (row) {
                        tinymce.DOM.add(row.parentNode, 'div', {
                            'style': 'float: right; padding: 3px;'
                        }, ed.getLang('wordcount.words', 'Words: ') + '<span id="' + t.id + '">0</span>');
                    }
                } else {
                    tinymce.DOM.add(id, 'span', {}, '<span id="' + t.id + '">0</span>');
                }
                t.elem = tinymce.DOM.get(t.id);
            });

            ed.onInit.add(function(ed) {
                ed.selection.onSetContent.add(function() {
                    t._count(ed);
                });

                t._count(ed);
            });

            ed.onSetContent.add(function(ed) {
                t._count(ed);
            });

            ed.onKeyUp.add(function(ed, e) {
                if (e.keyCode == last)
                    return;
                last = e.keyCode;

                for (var i=0; i<t.recalc_keys.length; i++) {
                    if (e.keyCode == t.recalc_keys[i]) {
                        t._count(ed);
                        return;
                    }
                }

            });
        },

        _count : function(ed) {
            var t = this, tc = 0;

            // Keep multiple calls from happening at the same time
            if (t.block)
                return;

            t.block = 1;

            setTimeout(function() {
                var tx = ed.getContent({
                    format : 'raw'
                });

                if (tx) {
                    tx = tx.replace(/<.[^<>]*?>/g, ' ').replace(/&nbsp;| /gi, ' '); // remove html tags and space chars
                    tx = tx.replace(t.cleanre, ''); // remove numbers and punctuation
                    tx.replace(t.countre, function() {
                        tc++;
                    }); // count the words
                }

                t.elem.innerHTML=tc.toString();
                if (t.limit > 0) t.elem.parentNode.className=tc > t.limit ? t.limit_class : '';

                setTimeout(function() {
                    t.block = 0;
                }, t.block_ms);
            }, 1);
        },

        getInfo: function() {
            return {
                longname : 'Word Count plugin',
                author : 'Moxiecode Systems AB',
                authorurl : 'http://tinymce.moxiecode.com',
                infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/wordcount',
                version : tinymce.majorVersion + "." + tinymce.minorVersion
            };
        }
    });

    tinymce.PluginManager.add('wordcount', tinymce.plugins.WordCount);
})();