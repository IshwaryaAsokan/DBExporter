select * from(select II.ITEM_NO, IA.ITEM_INFO_ID, IA.ATTRIBUTE_TYPE_ID, AT.ATTRIBUTE_TYPE as TYPE, IA.VALUE
from CB02kpna.ITEM_INFO ii, CB02kpna.item_attributes ia, CB02kpna.attribute_types at
where II.ITEM_INFO_ID = IA.ITEM_INFO_ID
and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
and II.ITEM_TYPE = 'G'
and II.STATUS_ID=1
order by II.ITEM_NO, IA.ATTRIBUTE_TYPE_ID) where rownum<=1000