// Apache TinkerPop3 Graph model-ADT
// Directed, binary, attributed multi-graph commonly known as a property graph
[define,graph<=vertex{*},
  vertex:('id'->obj,'label'->str,'properties'->property{*},'outE'->edge{*},'inE'->edge{*}),
  edge:('id'->obj,'label'->str,'properties'->property{*},'outV'->vertex,'inV'->vertex),
  property:([is,!['id'|'label']]->obj)]