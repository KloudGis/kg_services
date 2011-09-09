// ==========================================================================
// Project:   Kloudgis.Attrtype Fixtures
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

sc_require('models/attrtype');

Kloudgis.Attrtype.FIXTURES = [

// TODO: Add your data fixtures here.
// All fixture records must have a unique primary key (default 'guid').  See 
// the example below.
{
    guid: 1,
    name: "nolotocc",
    label: "No Lot Occupe",
    visible: YES,
    editable: NO
},

{
    guid: 2,
    name: "cleproprio",
    label: "Cleproprio Dwight",
    visible: NO,
    editable: YES
},

{
    guid: 3,
    name: "bornage",
    label: "Bornage",
    visible: YES,
    editable: YES
},
//
// { guid: 3,
//   firstName: "Jim",
//   lastName: "Halpert" },
//
// { guid: 4,
//   firstName: "Pam",
//   lastName: "Beesly" },
//
// { guid: 5,
//   firstName: "Ryan",
//   lastName: "Howard" }
];
