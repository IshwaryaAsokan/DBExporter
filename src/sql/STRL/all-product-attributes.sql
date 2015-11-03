select II.ITEM_NO, AT.ATTRIBUTE_TYPE as TYPE, IA.VALUE --5362
from CB02STRL.ITEM_INFO ii, CB02STRL.ITEM_ATTRIBUTES ia, CB02STRL.ATTRIBUTE_TYPES at
where II.ITEM_TYPE = 'G'
and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
and AT.ATTRIBUTE_TYPE_ID in (1886, 2029206) --gets us (Description_Thumbnail, ATG_Default_Category)
and ii.item_no in (
    WITH WEB_INCLUDED as 
        (Select Ii.Item_No
        From Cb02strl.Item_Info Ii, Cb02strl.Item_Attributes Ia, Cb02strl.Attribute_Types At
        Where Ii.Item_Type = 'G'
        And Ii.Item_Info_Id = Ia.Item_Info_Id
        And Ia.Attribute_Type_Id = At.Attribute_Type_Id
        And At.Attribute_Type_Id = 1046
        And Ia.Value = 'Yes'),
    PRICE_SPIDER_INCLUDED as
        (select II.ITEM_NO
        from CB02STRL.ITEM_INFO ii, CB02STRL.ITEM_ATTRIBUTES ia, CB02STRL.ATTRIBUTE_TYPES at
        where II.ITEM_TYPE = 'G'
        and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
        and IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID
        and AT.ATTRIBUTE_TYPE_ID = 2043681
        and IA.VALUE = 'Yes'),
    US_PRODUCT as
        (select ii1.item_no
        from CB02STRL.ITEM_INFO ii1, CB02STRL.ITEM_INFO ii2, CB02STRL.ITEM_GROUPS ig
        where IG.ITEM_GROUP_ID = II1.ITEM_INFO_ID
        and IG.ITEM_INFO_ID = II2.ITEM_INFO_ID
        and II1.ITEM_INFO_ID in (
            select II.ITEM_INFO_ID
            from CB02STRL.KEYWORD_PHRASES kp, CB02STRL.ITEM_INFO ii
            where (KP.PHRASE <> 'Canada')
            and KP.ITEM_INFO_ID = II.ITEM_INFO_ID))
    select * from WEB_INCLUDED intersect 
    select * from PRICE_SPIDER_INCLUDED intersect
    select * from US_PRODUCT)
