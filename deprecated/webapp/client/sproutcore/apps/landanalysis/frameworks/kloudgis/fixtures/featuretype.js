// ==========================================================================
// Project:   Kloudgis.Featuretype Fixtures
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

sc_require('models/featuretype');

Kloudgis.Featuretype.FIXTURES = [

  // TODO: Add your data fixtures here.
  // All fixture records must have a unique primary key (default 'guid').  See 
  // the example below.

   { guid: 1,
     name: "Lotoccupe",
     label: "Lot Occupé",
 	 attrtypes: [1,2]},
  
   { guid: 2,
     name: "Lottitre",
     label: "Lot Titre" },
  
   { guid: 3,
     name: "Proprietaire",
     label: "Propriétaire" },
  
   { guid: 4,
     name: "Acte",
     label: "Acte",
 	 attrtypes: [3]},
  
  // { guid: 5,
  //   firstName: "Ryan",
  //   lastName: "Howard" }

];
