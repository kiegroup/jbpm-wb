/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function (config) {
    // Define changes to default configuration here.
    // For the complete reference:
    // http://docs.ckeditor.com/#!/api/CKEDITOR.config

    config.removeButtons = 'Underline,Subscript,Superscript';

    // Se the most common block elements.
    config.format_tags = 'p;h1;h2;h3;pre';

    // Make dialogs simpler.
    config.removeDialogTabs = 'image:advanced;link:advanced';

    config.allowedContent = true;

    config.customConfig = '';
    config.width = '100%';
    config.height = 500;
    config.allowedContent = true;
    config.allowedContext = true;
    config.baseFloatZIndex = 20000002; // greater than modal dialog's
    config.resize_enabled = false;
    config.startupMode = 'wysiwyg';
    config.startupShowBorders = false;
    config.startupFocus = true;
    config.toolbarLocation = 'top';
    config.toolbarCanCollapse = false;
    config.toolbarStartupExpanded = true;
    config.toolbarGroups = [
        { name:'document', groups:[ 'document', 'doctools' ] },
        { name:'clipboard', groups:[ 'clipboard', 'undo' ] },
        { name:'forms' },
        { name:'tools' },
        '/',
        { name:'basicstyles', groups:[ 'basicstyles', 'cleanup' ] },
        { name:'paragraph', groups:[ 'list', 'indent', 'blocks', 'align' ] },
        '/',
        { name:'mode' },
        { name:'styles' },
        { name:'colors' },
        { name:'others' }
    ];


};
