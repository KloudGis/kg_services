
// This page describes a part of the interface for your application.
Webadmin.changePasswordPage = SC.Page.design({

    // The main signup pane.  used to show info
    mainPane: SC.PanelPane.design({
        layout: {
            centerX: 0,
            width: 320,
            centerY: 0,
            height: 160
        },

        defaultResponder: Webadmin,

        contentView: SC.View.design({

            childViews: "prompt okButton cancelButton passwordLabel password confirmLabel confirm".w(),

            // PROMPT
            prompt: SC.LabelView.design({
                layout: {
                    top: 12,
                    left: 20,
                    height: 18,
                    right: 20
                },
                value: "_users.Change Password".loc(),
            }),

            passwordLabel: SC.LabelView.design({
                layout: {
                    top: 40,
                    left: 20,
                    width: 100,
                    height: 18
                },
                textAlign: SC.ALIGN_RIGHT,
                value: "_users.NewPassword".loc(),
            }),

            password: SC.TextFieldView.design({
                layout: {
                    top: 40,
                    left: 130,
                    height: 20,
                    width: 150
                },
                hint: "_users.PasswordHint".loc(),
                isPassword: YES,
                valueBinding: "Webadmin.changePasswordController.password"
            }),

            confirmLabel: SC.LabelView.design({
                layout: {
                    top: 68,
                    left: 20,
                    width: 100,
                    height: 18
                },
                textAlign: SC.ALIGN_RIGHT,
                value: "_users.Confirm".loc(),
            }),

            confirm: SC.TextFieldView.design({
                layout: {
                    top: 68,
                    left: 130,
                    height: 20,
                    width: 150
                },
                isPassword: YES,
                valueBinding: "Webadmin.changePasswordController.confirmPassword"
            }),

            // BUTTONS
            okButton: SC.ButtonView.design({
                layout: {
                    bottom: 20,
                    right: 20,
                    width: 140,
                    height: 24
                },
                title: "_users.ChangePassword".loc(),
                isDefault: YES,
                action: "submitPassword"
            }),

            cancelButton: SC.ButtonView.design({
                layout: {
                    bottom: 20,
                    right: 170,
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
