[define,nat<=int[is>0]]
[define,person<=('name'->str,'age'->nat)]
[define,vertex<=person:('name'->str,'age'->nat)-<(
                        'id'    -> .age,
                        'label' -> <x>.name[plus,<.x>.age[as,str]],
                        'outE'  -> _{0})]
[define,vertex<=int-<('id'->_)]
