{
  "id": "d64e531d-8068-4767-9e7c-5e5b38a2a617",
  "name": "InvoiceLine",
  "model": {
    "className": "org.jbpm.console.ng.workbench.forms.display.backend.provider.model.InvoiceLine",
    "name": "invoiceLine",
    "formModelType": "org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel"
  },
  "fields": [
    {
      "maxLength": 100,
      "placeHolder": "Price",
      "annotatedId": false,
      "code": "TextBox",
      "id": "field_967364416942073E11",
      "name": "invoiceLine_price",
      "label": "Price",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "price",
      "standaloneClassName": "java.lang.Double",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxFieldDefinition"
    },
    {
      "maxLength": 100,
      "placeHolder": "Quantity",
      "annotatedId": false,
      "code": "TextBox",
      "id": "field_3640462042405056E12",
      "name": "invoiceLine_quantity",
      "label": "Quantity",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "quantity",
      "standaloneClassName": "java.lang.Integer",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxFieldDefinition"
    },
    {
      "maxLength": 100,
      "placeHolder": "Total",
      "annotatedId": false,
      "code": "TextBox",
      "id": "field_1007249188326785E12",
      "name": "invoiceLine_total",
      "label": "Total",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "total",
      "standaloneClassName": "java.lang.Double",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxFieldDefinition"
    },
    {
      "maxLength": 100,
      "placeHolder": "Product",
      "annotatedId": false,
      "code": "TextBox",
      "id": "field_876592430160613E11",
      "name": "invoiceLine_product",
      "label": "Product",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "product",
      "standaloneClassName": "java.lang.String",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxFieldDefinition"
    }
  ],
  "layoutTemplate": {
    "version": 1,
    "name": "InvoiceLine",
    "layoutProperties": {},
    "rows": [
      {
        "layoutColumns": [
          {
            "span": "3",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_876592430160613E11",
                  "form_id": "d64e531d-8068-4767-9e7c-5e5b38a2a617"
                }
              }
            ]
          },
          {
            "span": "3",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_967364416942073E11",
                  "form_id": "d64e531d-8068-4767-9e7c-5e5b38a2a617"
                }
              }
            ]
          },
          {
            "span": "3",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_3640462042405056E12",
                  "form_id": "d64e531d-8068-4767-9e7c-5e5b38a2a617"
                }
              }
            ]
          },
          {
            "span": "3",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_1007249188326785E12",
                  "form_id": "d64e531d-8068-4767-9e7c-5e5b38a2a617"
                }
              }
            ]
          }
        ]
      }
    ]
  }
}
