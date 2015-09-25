select II.ITEM_NO, AT.ATTRIBUTE_TYPE as TYPE, IA.VALUE
from CB02STRL.ITEM_INFO ii, CB02STRL.ITEM_ATTRIBUTES ia, CB02STRL.ATTRIBUTE_TYPES at
where II.ITEM_TYPE = 'G'
and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
and AT.ATTRIBUTE_TYPE_ID in (1886, 2029206) --gets us (Description_Thumbnail, ATG_Default_Category)
