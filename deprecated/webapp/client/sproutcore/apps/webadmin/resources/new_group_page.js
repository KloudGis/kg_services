sc_require('resources/strings');
// This page describes a part of the interface for your application.
Webadmin.newGroupPage = SC.Page.design({

    mainPane: SC.PanelPane.design({
        layout: {
            centerX: 0,
            width: 300,
            centerY: 0,
            height: 150
        },

        defaultResponder: Webadmin,
        contentView: SC.View.design({

            childViews: "prompt okButton cancelButton nameLabel name".w(),

            // PROMPT
            prompt: SC.LabelView.design({
                layout: {
                    top: 12,
                    left: 20,
                    height: 18,
                    right: 20
                },
                value: "_groups.Create a new Group".loc(),
            }),

            // INPUTS 
            nameLabel: SC.LabelView.design({
                layout: {
                    top: 40,
                    left: 20,
                    width: 100,
                    height: 18
                },
                textAlign: SC.ALIGN_RIGHT,
                value: "_groups.Name".loc(),
            }),

            name: SC.TextFieldView.design({
                layout: {
                    top: 40,
                    left: 130,
                    height: 20,
                    width: 150
                },
                valueBinding: "Webadmin.newGroupController.name"
            }),

            // BUTTONS
            okButton: SC.ButtonView.design({
                layout: {
                    bottom: 20,
                    right: 20,
                    width: 90,
                    height: 24
                },
                title: "_Create".loc(),
                isDefault: YES,
                action: "submitGroup"
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
                action: "cancelGroup"
            })

        })
    })

});
