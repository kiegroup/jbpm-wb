/**
 * Copyright (C) 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

if (!window.CKEditorHandler) {

    window.CKEditorHandler = {
        _defaultLanguage : 'en',

        /**
         * Returns a configuration instance for a CKEditor
         * @param title The title for the editor.
         * @param readonly Is editor in readonly mode?
         * @param tabIndex The tabulation index.
         * @param height The height for the editor.
         * @param size The width for the editor.
         * @param lang The language code for the editor.
         * @returns {{}}
         */
        configure : function(title,readonly,tabIndex,height,size, lang) {
            // alert('Configure instance. Title: ' + title + ' // readonly:' + readonly + ' // tabIndex:' + tabIndex + ' // height:' + height + ' // size:' + size + ' // lang:' + lang);
            var config = {};
            config.title = title;
            config.readOnly = readonly;
            config.tabIndex = tabIndex;
            config.height = height;
            config.width = size;
            config.defaultLanguage = this._defaultLanguage;
            config.language = lang;

            return config;
        },

        /**
         * Creates a new CKEditor instance.
         * @param uid
         * @param title The title for the editor.
         * @param readonly Is editor in readonly mode?
         * @param tabIndex The tabulation index.
         * @param height The height for the editor.
         * @param size The width for the editor.
         * @param lang The language code for the editor.
         * @param defaultText
         * @param valueDivId
         * @param maxlength
         * @returns {*}
         */
        create: function(uid, valueDivId, title,readonly,tabIndex,height,size, lang, maxlength) {
            // alert('Create instance. defaultText: ' + defaultText + ' // maxlength:' + maxlength);
            var config = this.configure(title, readonly,tabIndex, height,size, lang);
            var editor = CKEDITOR.replace( uid, config);

            // Add the change event handler.
            editor.on( 'change', function(e) {
                var valueDiv = document.getElementById(valueDivId);
                valueDiv.value = e.editor.getData();
                window.CKEditorHandler.checkMaxLength(e.editor, maxlength);
            });

            // Add the char counter
            setTimeout(function() {
                window.CKEditorHandler.CKEditorCharCounter.createCharCounter(editor, maxlength);
            }, 200);

            return editor;
        },

        /**
         * Checks if the editor instance has overvomed max character length limit.
         * If it does, the editor does not allow to type more characters.
         *
         * @param e The ck editor instance.
         * @param maxLength The maximum character length allowed.
         */
        checkMaxLength: function(e, maxLength) {
            setTimeout(function() {
                if (maxLength && maxLength >= 0) {
                    var l = window.CKEditorHandler.getLength(e);
                    if (l == maxLength)  e.fire("saveSnapshot");
                    else if (l > maxLength) e.execCommand("undo");
                    window.CKEditorHandler.CKEditorCharCounter.handleCharCounterMsg(e, l, maxLength);
                }
            }, 100);
        },

        /**
         * Return the CKEditor value.
         * @param e The CKEditor instance.
         * @returns {*}
         */
        getValue: function(e) {
            try {
                return e.getData();
            } catch (e) {}
            return '';
        },

        /**
         * Gets the length of the plain text of the ckeditor.
         * @param e The CKEditor instance.
         */
        getLength: function(e) {
            try {
                return e.document.getBody().getText().length;
            } catch (e) {}
            return 0;

        },

        /**
         * This object handles the custom char counter displayed in the bottom part of CKEditor screen.
         * The char counter is only shown if the attribute maxlength is set.
         */
        CKEditorCharCounter : {

            /**
             * Creates a new span element inside CKEditor that shows the char counter.
             *
             * @param e The CKEditor instance.
             * @param maxLength Tha maximum length allowed.
             */
            createCharCounter: function(e, maxLength) {
                if (maxLength >= 0) {
                    try {
                        // Add char count message
                        var id = e.element.getId();
                        var elem = document.getElementById('cke_' + id);
                        var bSpam = elem.getElementsByClassName('cke_bottom').item(0);
                        var charCountSpan = document.createElement('span');
                        charCountSpan.setAttribute("id",id + '_charCountSpan');
                        charCountSpan.innerHTML = this.getCharCounterText(0,maxLength);
                        bSpam.appendChild(charCountSpan);
                    }catch (ex) {}
                }
            },

            /**
             * Updates the char counter message..
             *
             * @param e The CKEditor instance.
             * @param current Tha current editor content length..
             * @param max Tha maximum length allowed.
             */
            handleCharCounterMsg: function(e, current, max) {
                if (max >= 0) {
                    try {
                        var id = e.element.getId();
                        var charCounter = document.getElementById(id + '_charCountSpan');
                        charCounter.innerHTML = this.getCharCounterText(current,max);
                    } catch (ex) {}
                }
            },

            /**
             * Get the char counter messag to show..
             *
             * @param current Tha current editor content length..
             * @param max Tha maximum length allowed.
             */
            getCharCounterText: function(current, max) {
                return '<p> ' + current + '/' + max + '</p>';
            }

        }
    }

}
