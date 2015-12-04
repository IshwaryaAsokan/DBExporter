select II.ITEM_NO, IA.ITEM_INFO_ID, IA.ATTRIBUTE_TYPE_ID, IA.VALUE, AT.ATTRIBUTE_TYPE as TYPE
from CB02RBRN.ITEM_INFO ii, CB02RBRN.item_attributes ia, CB02RBRN.attribute_types at
where II.ITEM_INFO_ID = IA.ITEM_INFO_ID
and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
order by IA.ITEM_INFO_ID
