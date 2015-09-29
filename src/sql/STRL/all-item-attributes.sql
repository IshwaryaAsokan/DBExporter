select II.ITEM_NO, AT.ATTRIBUTE_TYPE as TYPE, IA.VALUE
from CB02STRL.ITEM_INFO ii, CB02STRL.ITEM_ATTRIBUTES ia, CB02STRL.ATTRIBUTE_TYPES at
where II.ITEM_TYPE = 'I'
and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
and AT.ATTRIBUTE_TYPE_ID in (31, 2007156, 6) --gets us (UPC_Code, JPG_Item_Image, Color_Finish_Name)