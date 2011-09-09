
// This page describes a part of the interface for your application.
Webadmin.addPrivilegesPage = SC.Page.design({

    mainPane: SC.PanelPane.design({
        layout: {
            centerX: 0,
            width: 300,
            centerY: 0,
            height: 300
        },

        defaultResponder: Webadmin,
        contentView: SC.View.design({

            childViews: "prompt okButton cancelButton privilegesLabel privilegesScroll".w(),

            // PROMPT
            prompt: SC.LabelView.design({
                layout: {
                    top: 12,
                    left: 20,
                    height: 18,
                    right: 20
                },
                value: "_groups.addPrivileges.Add Privileges".loc(),
            }),

            // INPUTS 
            privilegesLabel: SC.LabelView.design({
                layout: {
                    top: 35,
                    left: 50,
                    width: 120,
                    height: 18
                },
                textAlign: SC.ALIGN_LEFT,
                value: "_groups.addPrivileges.Privileges".loc(),
            }),

           privilegesScroll : SC.ScrollView.design({
                layout: {
                    "top": 55,
                    "bottom": 60,
                    "left": 50,
                    "right": 50
                },
                backgroundColor: 'white',
                contentView: SC.ListView.design({
                    contentBinding: 'Webadmin.addPrivilegesController',
					selectionBinding: 'Webadmin.addPrivilegesController.selection',
                    contentValueKey: "name",
                    canDeleteContent: NO,
					hasContentIcon: NO,
                })
            }),

            // BUTTONS
            okButton: SC.ButtonView.design({
                layout: {
                    bottom: 20,
                    right: 20,
                    width: 90,
                    height: 24
                },
                title: "_groups.addPrivileges.Add".loc(),
                isDefault: YES,
                action: "addPrivileges"
            }),

            cancelButton: SC.ButtonView.design({
                layout: {
                    bottom: 20,
                    right: 120,
                    width: 90,
                    height: 24
                },
                title: "_Cancel".loc(),
                isCancel: YES,
                action: "cancelAddPrivileges"
            })

        })
    })
});
