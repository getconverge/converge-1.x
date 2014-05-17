/**
 */

(function() {

  tinymce.create('scriptito.plugins.TextMetrics', {
    
    block : 0,
    tid: null,
    ccid : null,
    wcid : null,
    scid : null,
    pcid : null,

    init : function(ed, url) {
    
      var t = this, last = 0;
      
      t.editor = ed;
      
      t.tid = ed.id + "-metrics";
      t.ccid = ed.id + '-character-count';
      t.wcid = ed.id + '-word-count';
      t.scid = ed.id + '-sentence-count';
      t.pcid = ed.id + '-paragraph-count';
  
      t.state = ed.getParam('s_textmetrics_on');
      
      ed.addCommand("s_toggleMetrics", t._toggleMetrics, t);
      
      ed.addButton('toggle_metrics', { title:'s_textmetrics.button_title', cmd:'s_toggleMetrics' });

      ed.onPostRender.add(function(ed, cm) {
        var row, id;
        id = ed.getParam('textmetrics_target_id');
        if (!id) {
          row = tinymce.DOM.get(ed.id + '_path_row');
          if (row) {
            tinymce.DOM.add(
              row.parentNode, 
              'div', 
              { 'id':t.tid, 'class':'metrics', 'style':'display:' + (t.state ? "block;" : "none;") }, 
              '<span>' + ed.getLang('s_textmetrics.characters', 'Characters: ') + '<span id="' + t.ccid + '">0</span></span>' +
              '<span>' + ed.getLang('s_textmetrics.words', 'Words: ') + '<span id="' + t.wcid + '">0</span></span>' +
              '<span>' + ed.getLang('s_textmetrics.sentences', 'Sentences: ') + '<span id="' + t.scid + '">0</span></span>' +
              '<span>' + ed.getLang('s_textmetrics.paragraphs', 'Paragraphs: ') + '<span id="' + t.pcid + '">0</span></span>');
          }
          ed.controlManager.setActive('toggle_metrics', t.state);
        } 
        else {
          tinymce.DOM.add(id, 'span', {}, '<span id="' + t.wcid + '">0</span>');
        }
      });

      ed.onSetContent.add(function(ed) {
        t._count(ed);
      });

      ed.onLoadContent.add(function(ed) {
        t._count(ed);
      });

      ed.onUndo.add(function(ed) {
        t._count(ed);
      });
      
      ed.onPaste.add(function(ed) {
        t._count(ed);
      });

      ed.onKeyUp.add(function(ed, e) {
        if (e.keyCode == last) {
          return;
        }
        if (sTextUtils.isWhitespaceKeyCode(e.keyCode) || sTextUtils.isPunctuationKeyCode(e.keyCode)
          || 8 == last // Backspace
          || 46 == last) { // Delete
          t._count(ed);
        }
        last = e.keyCode;
      });

    },

    _count : function(ed) {
      var t = this, tc = 0;
  
      if (!t.state || t.block)
        return;
  
      t.block = 1;
  
      setTimeout(function() {
        var tx = ed.getContent({format : 'raw'});
        if (tx) {
          var d = tinymce.DOM;
          var m = sTextUtils.getMetricsForHtml(tx);
          d.setHTML(t.ccid, m.characterCount);
          d.setHTML(t.wcid, m.wordCount);
          d.setHTML(t.scid, m.sentenceCount);
          d.setHTML(t.pcid, m.paragraphCount);
        }
        t.block = 0;
      }, 1);
    },

    _toggleMetrics:function() {
      var t = this, ed = t.editor;
      t.state = !t.state;
      ed.controlManager.setActive('toggle_metrics', t.state);
      tinymce.DOM.setStyle(t.tid, "display", t.state ? "block" : "none");
      if (t.state) {
        t._count(ed);
      }
     },
    
    getInfo: function() {
      return {
        longname : 'Text Metrics Plugin',
        author : 'Scriptito, LLC',
        authorurl : 'http://www.scriptito.com',
        infourl : 'http://www.scriptito.com',
        version : "1.2"
      };
    }
    
  });

  tinymce.PluginManager.add('s_textmetrics', scriptito.plugins.TextMetrics);

})();

/**
 * Copyright 2010, Scriptito LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

sTextUtils = {
    
  STATE_WHITESPACE:0,
  STATE_TAG:1,
  STATE_WORD:2,
  STATE_CHAR_REF:3,
  
  CHAR_CODE_AMP:"&".charCodeAt(0),
  CHAR_CODE_CARRIAGE:"\r".charCodeAt(0),
  CHAR_CODE_EXCLAIM:"!".charCodeAt(0),
  CHAR_CODE_GT:">".charCodeAt(0),
  CHAR_CODE_LT:"<".charCodeAt(0),
  CHAR_CODE_NEWLINE:"\n".charCodeAt(0),
  CHAR_CODE_PERIOD:".".charCodeAt(0),
  CHAR_CODE_QUESTION:"?".charCodeAt(0),
  CHAR_CODE_SEMI:";".charCodeAt(0),
  CHAR_CODE_SPACE:" ".charCodeAt(0),

  getMetricsForHtml:function(pText) {

    var lState = this.STATE_WHITESPACE;
    var lPrevState = this.STATE_WHITESPACE;
    var lCharCount = 0;
    var lWhitespaceCount = 0;
    var lWordCount = 0;
    var lSentenceCount = 0;
    var lParagraphCount = 0;
    var lSentenceChars = 0;
    var lBufferStart = 0;
    var lBufferLen = 0;
    var lBuffer = "";

    for (var i = 0; i < pText.length; i++)
    {
      var lCharCode = pText.charCodeAt(i);
      
      switch (lState) {
        case this.STATE_WHITESPACE : 
          if (lCharCode == this.CHAR_CODE_LT) {
            lBufferStart = i;
            lPrevState = lState;
            lState = this.STATE_TAG;
          }
          else 
          {
            lCharCount++;
            if (lCharCode == this.CHAR_CODE_AMP) {
              lBufferStart = i;
              lPrevState = lState;
              lState = this.STATE_CHAR_REF;
            }
            else if (this.isPunctuation(lCharCode)) {
              // Nothing to do, but prevent next 2 from being invoked
            }
            else if (!this.isWhitespace(lCharCode)) {
              lWordCount++;
              lSentenceChars++;
              if (lSentenceChars == 1) {
                lSentenceCount++;
              }
              lState = this.STATE_WORD;
            }
            else {
              lWhitespaceCount++;
            }
          }
          break;
        case this.STATE_TAG :
          if (lCharCode == this.CHAR_CODE_GT) {
            var lBuffer = pText.substring(lBufferStart + 1, i).toLowerCase();
            if (lBuffer == "p" || lBuffer == "li") {
              lParagraphCount++;
              lSentenceChars = 0;
              lState = this.STATE_WHITESPACE;
            }
            else {
              lState = lPrevState;
            }
          }
          break; 
        case this.STATE_CHAR_REF :
          if (lCharCode == this.CHAR_CODE_SEMI) {
            var lBuffer = pText.substring(lBufferStart + 1, i).toLowerCase();
            if (lBuffer == "nbsp") {
              lWhitespaceCount++;
              lState = this.STATE_WHITESPACE;
            }
            else {
              if (lPrevState == this.STATE_WHITESPACE) {
                lWordCount++;
              }
              lState = this.STATE_WORD;
            }
          }
          break;
        case this.STATE_WORD :
          if (lCharCode == this.CHAR_CODE_LT) {
            lBufferStart = i;
            lPrevState = lState;
            lState = this.STATE_TAG;
          }
          else {
            lCharCount++;
            lSentenceChars++;
            if (lCharCode == this.CHAR_CODE_AMP) {
              lBufferStart = i;
              lPrevState = lState;
              lState = this.STATE_CHAR_REF;
            }
            else if (this.isPunctuation(lCharCode)) {
              lSentenceChars = 0;
              lState = this.STATE_WHITESPACE;
            }
            else if (this.isWhitespace(lCharCode)) {
              lWhitespaceCount++;
              lState = this.STATE_WHITESPACE;
            }
          }
          break;
        default:
          // TODO throw exception - illegal state
      }
    }

    return {
      "characterCount":lCharCount,
      "paragraphCount":lParagraphCount,
      "sentenceCount":lSentenceCount,
      "whitespaceCount":lWhitespaceCount,
      "wordCount":lWordCount 
    };
  },
  
  isPunctuation:function(pCharCode) {
    return pCharCode == this.CHAR_CODE_PERIOD || pCharCode == this.CHAR_CODE_EXCLAIM || pCharCode == this.CHAR_CODE_QUESTION;
  },
  
  isPunctuationKeyCode:function(pKeyCode) {
    return (pKeyCode == 190 || pKeyCode == 46) || pKeyCode == 49 || (pKeyCode == 191 || pKeyCode == 47);
  },
  
  isWhitespace:function(pCharCode) {
    return pCharCode == this.CHAR_CODE_SPACE || pCharCode == this.CHAR_CODE_NEWLINE || pCharCode == this.CHAR_CODE_CARRIAGE;
  },
  
  isWhitespaceKeyCode:function(pKeyCode) {
    return pKeyCode == 13 || pKeyCode == 32;
  }
  
};