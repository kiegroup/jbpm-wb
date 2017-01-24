{
  "id": "bfc2a2bb-9262-4545-b651-28b48de86b9c",
  "name": "Client",
  "model": {
    "className": "org.jbpm.workbench.forms.display.backend.provider.model.Client",
    "name": "client",
    "formModelType": "org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel"
  },
  "fields": [
    {
      "maxLength": 100,
      "placeHolder": "ID",
      "annotatedId": false,
      "code": "TextBox",
      "id": "field_008747909183273E11",
      "name": "client_id",
      "label": "ID",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "id",
      "standaloneClassName": "java.lang.Long",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition"
    },
    {
      "maxLength": 100,
      "placeHolder": "Name",
      "annotatedId": false,
      "code": "TextBox",
      "id": "field_2119306785843892E12",
      "name": "client_name",
      "label": "Name",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "name",
      "standaloneClassName": "java.lang.String",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition"
    },
    {
      "placeHolder": "Address",
      "rows": 4,
      "annotatedId": false,
      "code": "TextArea",
      "id": "field_1133645106306838E12",
      "name": "client_address",
      "label": "Address",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "address",
      "standaloneClassName": "java.lang.String",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition"
    }
  ],
  "layoutTemplate": {
    "version": 1,
    "name": "Client",
    "layoutProperties": {},
    "rows": [
      {
        "layoutColumns": [
          {
            "span": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_008747909183273E11",
                  "form_id": "bfc2a2bb-9262-4545-b651-28b48de86b9c"
                }
              }
            ]
          }
        ]
      },
      {
        "layoutColumns": [
          {
            "span": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_2119306785843892E12",
                  "form_id": "bfc2a2bb-9262-4545-b651-28b48de86b9c"
                }
              }
            ]
          }
        ]
      },
      {
        "layoutColumns": [
          {
            "span": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_1133645106306838E12",
                  "form_id": "bfc2a2bb-9262-4545-b651-28b48de86b9c"
                }
              }
            ]
          }
        ]
      }
    ]
  }
}
