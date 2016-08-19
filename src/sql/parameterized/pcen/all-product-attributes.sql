select II.ITEM_NO, IA.ITEM_INFO_ID, IA.ATTRIBUTE_TYPE_ID, AT.ATTRIBUTE_TYPE as TYPE, IA.VALUE
from CB02{{business}}.ITEM_INFO ii, CB02{{business}}.item_attributes ia, CB02{{business}}.attribute_types at
where II.ITEM_INFO_ID = IA.ITEM_INFO_ID
and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
and II.ITEM_TYPE = 'G'
order by II.ITEM_NO, IA.ATTRIBUTE_TYPE_ID
