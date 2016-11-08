{
  "id": "b0224ad9-8e69-4037-8ed6-8cd6c46fc879",
  "name": "Invoice",
  "model": {
    "className": "org.jbpm.console.ng.workbench.forms.display.backend.provider.model.Invoice",
    "name": "invoice",
    "formModelType": "org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel"
  },
  "fields": [
    {
      "placeHolder": "Comments",
      "rows": 4,
      "annotatedId": false,
      "code": "TextArea",
      "id": "field_0065289253337462E12",
      "name": "invoice_comments",
      "label": "Comments",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "comments",
      "standaloneClassName": "java.lang.String",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.basic.textArea.TextAreaFieldDefinition"
    },
    {
      "nestedForm": "bfc2a2bb-9262-4545-b651-28b48de86b9c",
      "annotatedId": false,
      "code": "SubForm",
      "id": "field_027522630601456E11",
      "name": "invoice_client",
      "label": "Client Data",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "client",
      "standaloneClassName": "org.jbpm.console.ng.workbench.forms.display.backend.provider.model.Client",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition"
    },
    {
      "placeHolder": "Date",
      "showTime": true,
      "annotatedId": false,
      "code": "DatePicker",
      "id": "field_746962049072168E11",
      "name": "invoice_date",
      "label": "Date",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "date",
      "standaloneClassName": "java.util.Date",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.basic.datePicker.DatePickerFieldDefinition"
    },
    {
      "creationForm": "d64e531d-8068-4767-9e7c-5e5b38a2a617",
      "editionForm": "d64e531d-8068-4767-9e7c-5e5b38a2a617",
      "columnMetas": [
        {
          "label": "Quant.",
          "property": "quantity"
        },
        {
          "label": "Product",
          "property": "product"
        },
        {
          "label": "Price",
          "property": "price"
        },
        {
          "label": "Total",
          "property": "total"
        }
      ],
      "annotatedId": false,
      "code": "MultipleSubForm",
      "id": "field_120824347107582E12",
      "name": "invoice_lines",
      "label": "Lines",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "lines",
      "standaloneClassName": "org.jbpm.console.ng.workbench.forms.display.backend.provider.model.InvoiceLine",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition"
    },
    {
      "maxLength": 100,
      "placeHolder": "Total",
      "annotatedId": false,
      "code": "TextBox",
      "id": "field_115459324850379E11",
      "name": "invoice_total",
      "label": "Total",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "total",
      "standaloneClassName": "java.lang.Double",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxFieldDefinition"
    }
  ],
  "layoutTemplate": {
    "version": 1,
    "name": "Invoice",
    "layoutProperties": {},
    "rows": [
      {
        "layoutColumns": [
          {
            "span": "6",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_027522630601456E11",
                  "form_id": "b0224ad9-8e69-4037-8ed6-8cd6c46fc879"
                }
              }
            ]
          },
          {
            "span": "6",
            "rows": [
              {
                "layoutColumns": [
                  {
                    "span": "12",
                    "rows": [],
                    "layoutComponents": [
                      {
                        "dragTypeName": "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent",
                        "properties": {
                          "HTML_CODE": ""
                        }
                      }
                    ]
                  }
                ]
              }
            ],
            "layoutComponents": []
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
                  "field_id": "field_746962049072168E11",
                  "form_id": "b0224ad9-8e69-4037-8ed6-8cd6c46fc879"
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
                  "field_id": "field_120824347107582E12",
                  "form_id": "b0224ad9-8e69-4037-8ed6-8cd6c46fc879"
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
                  "field_id": "field_115459324850379E11",
                  "form_id": "b0224ad9-8e69-4037-8ed6-8cd6c46fc879"
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
                  "field_id": "field_0065289253337462E12",
                  "form_id": "b0224ad9-8e69-4037-8ed6-8cd6c46fc879"
                }
              }
            ]
          }
        ]
      }
    ]
  }
}
