--ad copy is at parent level
--this needs adjustment
select II.ITEM_NO, ACT.AD_COPY_TYPE as TYPE, ACC.AD_COPY as VALUE
from CB02KPNA.ITEM_INFO ii, CB02KPNA.AD_COPY ac, CB02KPNA.AD_COPY_TYPES act, CB02KPNA.AD_COPY_CHUNKS acc
where II.ITEM_INFO_ID = AC.ITEM_INFO_ID
and AC.AD_COPY_TYPE_ID = ACT.AD_COPY_TYPE_ID
and ACC.AD_COPY_ID = AC.AD_COPY_ID
and II.ITEM_NO in 
        (with UNRELEASED as
            (select ii.item_no
            from CB02KPNA.ITEM_INFO ii, cb02kpna.item_attributes ia, cb02kpna.attribute_types at
            where AT.ATTRIBUTE_TYPE_ID = IA.ATTRIBUTE_TYPE_ID
            and II.ITEM_TYPE = 'I'
            and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
            and AT.ATTRIBUTE_TYPE_ID = 2001056 
            and IA.VALUE = 'No'), 
        INVALID_ROOTVAR as 
            (select ii.item_no
            from CB02KPNA.ITEM_INFO ii, cb02kpna.item_attributes ia, cb02kpna.attribute_types at
            where AT.ATTRIBUTE_TYPE_ID = IA.ATTRIBUTE_TYPE_ID
            and II.ITEM_TYPE = 'I'
            and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
            and AT.ATTRIBUTE_TYPE_ID = 1046
            and IA.VALUE = 'No'),
        DISCONTINUED as 
            (select ii.item_no
            from CB02KPNA.ITEM_INFO ii, cb02kpna.item_attributes ia, cb02kpna.attribute_types at
            where AT.ATTRIBUTE_TYPE_ID = IA.ATTRIBUTE_TYPE_ID
            and II.ITEM_TYPE = 'I'
            and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
            and AT.ATTRIBUTE_TYPE_ID = 21
            and TO_DATE(IA.VALUE, 'MM/DD/YYYY') <= sysdate),
        ATG_INACTVE as
            (select ii2.item_no
            from CB02KPNA.ITEM_INFO ii1, CB02KPNA.ITEM_INFO ii2, CB02KPNA.ITEM_GROUPS ig
            where IG.ITEM_GROUP_ID = II1.ITEM_INFO_ID
            and IG.ITEM_INFO_ID = II2.ITEM_INFO_ID
            and II1.ITEM_INFO_ID in (
                select ii.item_info_id
                from CB02KPNA.ITEM_INFO ii, cb02kpna.item_attributes ia, cb02kpna.attribute_types at
                where AT.ATTRIBUTE_TYPE_ID = IA.ATTRIBUTE_TYPE_ID
                and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
                and AT.ATTRIBUTE_TYPE_ID = 2031006
                and IA.VALUE = 'No')), 
        KOHLER_STORE_ITEMS as 
            (select ii2.item_no
            from CB02KPNA.ITEM_INFO ii1, CB02KPNA.ITEM_INFO ii2, CB02KPNA.ITEM_GROUPS ig
            where IG.ITEM_GROUP_ID = II1.ITEM_INFO_ID
            and IG.ITEM_INFO_ID = II2.ITEM_INFO_ID
            and II1.ITEM_INFO_ID in (
                select II.ITEM_INFO_ID
                from CB02KPNA.ITEM_INFO ii, cb02kpna.item_attributes ia, cb02kpna.attribute_types at
                where AT.ATTRIBUTE_TYPE_ID = IA.ATTRIBUTE_TYPE_ID
                and II.ITEM_INFO_ID = IA.ITEM_INFO_ID
                and AT.ATTRIBUTE_TYPE_ID = 2029206
                and ia.value = 'Kohler Store')),
        BAD_SECTION as 
            (select ii2.item_no
            from CB02KPNA.ITEM_INFO ii1, CB02KPNA.ITEM_INFO ii2, CB02KPNA.ITEM_GROUPS ig
            where IG.ITEM_GROUP_ID = II1.ITEM_INFO_ID
            and IG.ITEM_INFO_ID = II2.ITEM_INFO_ID
            and II1.ITEM_INFO_ID in (
                select II.ITEM_INFO_ID
                from CB02KPNA.KEYWORDS kw, CB02KPNA.KEYWORD_PHRASES kwp, CB02KPNA.ITEM_INFO ii
                where KW.KEYWORD_TYPE_ID = 1146
                and II.ITEM_INFO_ID = KWP.ITEM_INFO_ID    
                and KWP.PHRASE in ('MODS', 'Service Parts', 'Special Mod', 'Project Packs', 'EQ Specs - Shower Doors')
                and KW.PHRASE_ID = KWP.PHRASE_ID)),
        SOLD_IN_USA as
            (select ii2.item_no
            from CB02KPNA.ITEM_INFO ii1, CB02KPNA.ITEM_INFO ii2, CB02KPNA.ITEM_GROUPS ig
            where IG.ITEM_GROUP_ID = II1.ITEM_INFO_ID
            and IG.ITEM_INFO_ID = II2.ITEM_INFO_ID
            and II1.ITEM_INFO_ID in (
                select II.ITEM_INFO_ID
                from CB02KPNA.KEYWORD_PHRASES kp, CB02KPNA.ITEM_INFO ii
                where (KP.PHRASE = 'Both'
                or KP.PHRASE = 'US')
                and KP.ITEM_INFO_ID = II.ITEM_INFO_ID)),
        ALL_ITEMS as
            (select ii.item_no
            from CB02KPNA.ITEM_INFO ii
            where II.ITEM_TYPE = 'I')
        select * from ALL_ITEMS
        minus select * from UNRELEASED
        minus select * from INVALID_ROOTVAR
        minus select * from DISCONTINUED
        minus select * from ATG_INACTVE
        minus select * from KOHLER_STORE_ITEMS
        minus select * from BAD_SECTION
        intersect select * from SOLD_IN_USA)
