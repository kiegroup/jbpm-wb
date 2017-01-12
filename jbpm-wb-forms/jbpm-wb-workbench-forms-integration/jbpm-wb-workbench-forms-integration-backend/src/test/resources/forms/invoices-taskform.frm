{
  "id": "b5456167-901b-4379-9ecf-a7423fdbf906",
  "name": "invoices-taskform",
  "model": {
    "processId": "invoices",
    "processName": "invoices",
    "variables": [
      {
        "name": "invoice",
        "type": "org.jbpm.console.ng.workbench.forms.display.backend.provider.model.Invoice"
      }
    ],
    "formModelType": "org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel"
  },
  "fields": [
    {
      "nestedForm": "b0224ad9-8e69-4037-8ed6-8cd6c46fc879",
      "annotatedId": false,
      "code": "SubForm",
      "id": "field_1554166226711648E12",
      "name": "invoice",
      "label": "Invoice",
      "required": false,
      "readonly": false,
      "validateOnChange": true,
      "binding": "invoice",
      "standaloneClassName": "org.jbpm.console.ng.workbench.forms.display.backend.provider.model.Invoice",
      "serializedFieldClassName": "org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition"
    }
  ],
  "layoutTemplate": {
    "version": 1,
    "name": "invoices-taskform",
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
                  "field_id": "field_1554166226711648E12",
                  "form_id": "b5456167-901b-4379-9ecf-a7423fdbf906"
                }
              }
            ]
          }
        ]
      }
    ]
  }
}
