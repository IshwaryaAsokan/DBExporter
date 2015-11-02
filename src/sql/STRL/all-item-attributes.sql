select II.ITEM_NO, AT.ATTRIBUTE_TYPE as TYPE, IA.VALUE
from CB02STRL.ITEM_INFO ii, CB02STRL.ITEM_ATTRIBUTES ia, CB02STRL.ATTRIBUTE_TYPES at
where II.ITEM_TYPE = 'I'
and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
and AT.ATTRIBUTE_TYPE_ID in (31, 2007156, 6) --gets us (UPC_Code, JPG_Item_Image, Color_Finish_Name)
and II.ITEM_NO in (
    WITH DISCONTINUED as
        (select ii1.item_no
        from CB02STRL.ITEM_INFO ii1, CB02STRL.ITEM_INFO ii2, CB02STRL.item_attributes ia, CB02STRL.attribute_types at, CB02STRL.ITEM_GROUPS ig
        where IG.ITEM_INFO_ID = II1.ITEM_INFO_ID
        and IG.ITEM_GROUP_ID = II2.ITEM_INFO_ID
        and AT.ATTRIBUTE_TYPE_ID = IA.ATTRIBUTE_TYPE_ID
        and II1.ITEM_TYPE = 'I'
        and II1.ITEM_INFO_ID = IA.ITEM_INFO_ID
        and AT.ATTRIBUTE_TYPE_ID = 21),
    ALL_ITEMS as 
        (select II.ITEM_NO
        from CB02STRL.ITEM_INFO ii
        where II.ITEM_TYPE = 'I'),
    RELEASED as
        (select ii1.item_no
        from CB02STRL.ITEM_INFO ii1, CB02STRL.ITEM_INFO ii2, CB02STRL.item_attributes ia, CB02STRL.attribute_types at, CB02STRL.ITEM_GROUPS ig
        where IG.ITEM_INFO_ID = II1.ITEM_INFO_ID
        and IG.ITEM_GROUP_ID = II2.ITEM_INFO_ID
        and AT.ATTRIBUTE_TYPE_ID = IA.ATTRIBUTE_TYPE_ID
        and II1.ITEM_TYPE = 'I'
        and II1.ITEM_INFO_ID = IA.ITEM_INFO_ID
        and AT.ATTRIBUTE_TYPE_ID = 2001056
        and IA.VALUE <> 'No'
        and TO_DATE(IA.VALUE, 'MM/DD/YYYY') <= sysdate)
    select * from ALL_ITEMS minus
    select * from DISCONTINUED intersect
    select * from RELEASED)
