[define,vertex<=person:('name'->str,'age'->int,'knows'->int)<x>-<
  ('name'    ->.name,
   'friends' ->[=mmkv,''][is,.k==x.knows].v
  )]