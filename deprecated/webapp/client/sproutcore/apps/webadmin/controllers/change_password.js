// ==========================================================================
// Project:   Webadmin.newUserController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */


Webadmin.changePasswordController = SC.ObjectController.create(
/** @scope Webadmin.newUserController.prototype */
{

    confirmPassword: null,
    sheetPane: null,

    isValidUser: function() {
        var uNew = this.get('content');
        var password = uNew.get('password');
        if (SC.isEqual(password, this.confirmPassword)) {
            return YES;
        } else {
            this.showSheetError('_users.PasswordsDontMatch'.loc());
            return NO;
        }
    },

    showSheetError: function(message) {
        var sheet = SC.SheetPane.create({
            layout: {
                width: 430,
                height: 150,
                centerX: 0,
            },
            contentView: SC.View.extend({
                layout: {
                    top: 0,
                    left: 0,
                    bottom: 0,
                    right: 0
                },
                childViews: 'iconView labelView okButtonView'.w(),

				iconView: SC.ImageView.extend({
                    layout: {
                        centerY: -10,
                        height: 48,
                        left: 20,
                        width: 48
                    },
                    value: "sc-icon-error-48",
                }),

                labelView: SC.LabelView.extend({
                    layout: {
                        centerY: -10,
                        height: 24,
                        left: 0,
                        right: 0
                    },
                    textAlign: SC.ALIGN_CENTER,
                    controlSize: SC.LARGE_CONTROL_SIZE,
                    value: message
                }),

                okButtonView: SC.ButtonView.extend({
                    layout: {
                        width: 80,
                        bottom: 20,
                        height: 24,
                        centerX: 0
                    },
                    title: '_OK'.loc(),
                    isDefault: YES,
                    action: "remove",
                    target: "Webadmin.changePasswordController.sheetPane"
                })
            })
        });
        sheet.append();
        this.set('sheetPane', sheet);
    },

});
