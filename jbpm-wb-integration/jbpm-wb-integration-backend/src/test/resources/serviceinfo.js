var serviceinfo = [
{
        "name" : "DeleteVimeo",
        "displayName" : "DeleteVimeo",
        "defaultHandler" : "mvel: new org.jbpm.process.workitem.vimeo.DeleteVideoWorkitemHandler(\"accessToken\")",
        "documentation" : "vimeo-workitem/index.html",
        "module" : "vimeo-workitem",
        "icon" : "DeleteVimeo.png",

            "category" : "Vimeo",
            "description" : "Interact with videos on Vimeo",
            "keywords" : [
                                "vimeo"
                ,"video"
                ,"delete"

            ],
                "isaction" : "true",
                "actiontitle" : "Delete an existing video",
            "requiresauth" : "true",
                "authparams" : [
                                           {
                                               "name" : "Vimeo access token"
                                           }

                ],
                "authreferencesite" : "https://developer.vimeo.com/api/authentication",
            "parameters" : [
                                   {
                                       "name" : "VideoEndpoint",
                                       "type" : "new StringDataType()"
                                   }

            ],
            "results" : [
                                   {
                                       "name" : "ResponseStatusCode",
                                       "type" : "new StringDataType()"
                                   }

            ],
            "mavenDependencies" : [
                                   {
                                       "groupId" : "org.jbpm.contrib",
                                       "artifactId" : "vimeo-workitem",
                                       "version" : "7.29.0-SNAPSHOT"
                                   }

            ]
}
    ,{
        "name" : "DeleteVimeo",
        "displayName" : "DeleteVimeo",
        "defaultHandler" : "mvel: new org.jbpm.process.workitem.vimeo.DeleteVideoWorkitemHandler(\"accessToken\")",
        "documentation" : "vimeo-workitem/index.html",
        "module" : "vimeo-workitem",
        "icon" : "DeleteVimeo.png",

        "category" : "Vimeo",
        "description" : "Interact with videos on Vimeo",
        "keywords" : [
            "vimeo"
            ,"video"
            ,"delete"

        ],
        "isaction" : "true",
        "actiontitle" : "Delete an existing video",
        "requiresauth" : "true",
        "authparams" : [
            {
                "name" : "Vimeo access token"
            }

        ],
        "authreferencesite" : "https://developer.vimeo.com/api/authentication",
        "parameters" : [
            {
                "name" : "VideoEndpoint",
                "type" : "new StringDataType()"
            }

        ],
        "results" : [
            {
                "name" : "ResponseStatusCode",
                "type" : "new StringDataType()"
            }

        ],
        "mavenDependencies" : [
            {
                "groupId" : "org.jbpm.contrib",
                "artifactId" : "vimeo-workitem",
                "version" : "7.30.0-SNAPSHOT"
            }

        ]
    }
,{
        "name" : "GetInfoVimeo",
        "displayName" : "GetInfoVimeo",
        "defaultHandler" : "mvel: new org.jbpm.process.workitem.vimeo.GetVideoInfoWorkitemHandler(\"accessToken\")",
        "documentation" : "vimeo-workitem/index.html",
        "module" : "vimeo-workitem",
        "icon" : "GetInfoVimeo.png",
                            "gav" : "org.jbpm.contrib:vimeo-workitem:7.29.0-SNAPSHOT",

            "category" : "Vimeo",
            "description" : "Interact with videos on Vimeo",
            "keywords" : [
                                "vimeo"
                ,"video"
                ,"get"
                ,"info"

            ],
                "isaction" : "true",
                "actiontitle" : "Get info for existing video",
            "requiresauth" : "true",
                "authparams" : [
                                           {
                                               "name" : "Vimeo access token"
                                           }

                ],
                "authreferencesite" : "https://developer.vimeo.com/api/authentication",
            "parameters" : [
                                   {
                                       "name" : "VideoEndpoint",
                                       "type" : "new StringDataType()"
                                   }

            ],
            "results" : [
                                   {
                                       "name" : "VideoInfo",
                                       "type" : "new StringDataType()"
                                   }

            ],
            "mavenDependencies" : [
                                   {
                                       "groupId" : "org.jbpm.contrib",
                                       "artifactId" : "vimeo-workitem",
                                       "version" : "7.29.0-SNAPSHOT"
                                   }

            ]
}

,{
        "name" : "GetInfoVimeo",
        "displayName" : "GetInfoVimeo",
        "defaultHandler" : "mvel: new org.jbpm.process.workitem.vimeo.GetVideoInfoWorkitemHandler(\"accessToken\")",
        "documentation" : "vimeo-workitem/index.html",
        "module" : "vimeo-workitem",
        "icon" : "GetInfoVimeo.png",
        "gav" : "org.jbpm.contrib:vimeo-workitem:7.30.0-SNAPSHOT",

        "category" : "Vimeo",
        "description" : "Interact with videos on Vimeo",
        "keywords" : [
            "vimeo"
            ,"video"
            ,"get"
            ,"info"

        ],
        "isaction" : "true",
        "actiontitle" : "Get info for existing video",
        "requiresauth" : "true",
        "authparams" : [
            {
                "name" : "Vimeo access token"
            }

        ],
        "authreferencesite" : "https://developer.vimeo.com/api/authentication",
        "parameters" : [
            {
                "name" : "VideoEndpoint",
                "type" : "new StringDataType()"
            }

        ],
        "results" : [
            {
                "name" : "VideoInfo",
                "type" : "new StringDataType()"
            }

        ],
        "mavenDependencies" : [
            {
                "groupId" : "org.jbpm.contrib",
                "artifactId" : "vimeo-workitem",
                "version" : "7.30.0-SNAPSHOT"
            }

        ]
    }
    ,
]
