select II.ITEM_NO, IA.ITEM_INFO_ID, IA.ATTRIBUTE_TYPE_ID, AT.ATTRIBUTE_TYPE, IA.VALUE
from CB02BAKR.ITEM_INFO ii, CB02BAKR.item_attributes ia, CB02BAKR.attribute_types at
where II.ITEM_INFO_ID = IA.ITEM_INFO_ID
and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
and II.ITEM_TYPE = 'I'
order by II.ITEM_NO, IA.ATTRIBUTE_TYPE_ID
