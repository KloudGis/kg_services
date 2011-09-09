
// This page describes a part of the interface for your application.
Webadmin.addRolesPage = SC.Page.design({

    mainPane: SC.PanelPane.design({
        layout: {
            centerX: 0,
            width: 275,
            centerY: 0,
            height: 200
        },

        defaultResponder: Webadmin,
        contentView: SC.View.design({

            childViews: "prompt okButton cancelButton rolesLabel rolesScroll".w(),

            // PROMPT
            prompt: SC.LabelView.design({
                layout: {
                    top: 12,
                    left: 20,
                    height: 18,
                    right: 20
                },
                value: "_users.addRoles.Add Roles".loc(),
            }),

            // INPUTS 
            rolesLabel: SC.LabelView.design({
                layout: {
                    top: 35,
                    left: 50,
                    width: 200,
                    height: 18
                },
                textAlign: SC.ALIGN_LEFT,
                value: "_users.addRoles.Roles".loc(),
            }),

           rolesScroll : SC.ScrollView.design({
                layout: {
                    "top": 55,
                    "bottom": 60,
                    "left": 50,
                    "right": 50
                },
                backgroundColor: 'white',
                contentView: SC.ListView.design({
                    contentBinding: 'Webadmin.addRolesController',
					selectionBinding: 'Webadmin.addRolesController.selection',
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
                title: "_users.addRoles.Add".loc(),
                isDefault: YES,
                action: "addRoles"
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
                action: "cancelAddRoles"
            })

        })
    })
});
