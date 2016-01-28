select II.ITEM_NO, AT.ATTRIBUTE_TYPE as TYPE, IA.VALUE 
from CB02KPNA.ITEM_INFO ii, cb02kpna.item_attributes ia, cb02kpna.attribute_types at
where II.ITEM_INFO_ID = IA.ITEM_INFO_ID
and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
and II.ITEM_NO in (select * from CB02KPNA.KPNA_WEB_VALID_SKUS)
