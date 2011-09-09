sc_require('resources/strings');
// This page describes a part of the interface for your application.
Webadmin.newUserPage = SC.Page.design({

    mainPane: SC.PanelPane.design({
        layout: {
            centerX: 0,
            width: 300,
            centerY: 0,
            height: 210
        },

        defaultResponder: Webadmin,
        contentView: SC.View.design({

            childViews: "prompt okButton cancelButton nameLabel name passwordLabel password confirmLabel confirm expireLabel expire".w(),

            // PROMPT
            prompt: SC.LabelView.design({
                layout: {
                    top: 12,
                    left: 20,
                    height: 18,
                    right: 20
                },
                value: "_users.Create a new User".loc(),
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
                value: "_users.Name".loc(),
            }),

            name: SC.TextFieldView.design({
                layout: {
                    top: 40,
                    left: 130,
                    height: 20,
                    width: 150
                },
                valueBinding: "Webadmin.newUserController.name"
            }),

            passwordLabel: SC.LabelView.design({
                layout: {
                    top: 68,
                    left: 20,
                    width: 100,
                    height: 18
                },
                textAlign: SC.ALIGN_RIGHT,
                value: "_users.Password".loc(),
            }),

            password: SC.TextFieldView.design({
                layout: {
                    top: 68,
                    left: 130,
                    height: 20,
                    width: 150
                },
                hint: "_users.PasswordHint".loc(),
                isPassword: YES,
                valueBinding: "Webadmin.newUserController.password"
            }),

            confirmLabel: SC.LabelView.design({
                layout: {
                    top: 100,
                    left: 20,
                    width: 100,
                    height: 18
                },
                textAlign: SC.ALIGN_RIGHT,
                value: "_users.Confirm".loc(),
            }),

            confirm: SC.TextFieldView.design({
                layout: {
                    top: 100,
                    left: 130,
                    height: 20,
                    width: 150
                },
                isPassword: YES,
                valueBinding: "Webadmin.newUserController.confirmPassword"
            }),

            expireLabel: SC.LabelView.design({
                layout: {
                    top: 128,
                    left: 20,
                    width: 100,
                    height: 18
                },
                textAlign: SC.ALIGN_RIGHT,
                value: "_users.Expire".loc(),
            }),

            expire: SC.TextFieldView.design({
                layout: {
                    top: 128,
                    left: 130,
                    height: 20,
                    width: 150
                },
                hint: "_users.ExpireHint".loc(),
                valueBinding: "Webadmin.newUserController.expireDate"
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
                action: "submitUser"
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
                action: "cancel"
            })

        })
    })

});
