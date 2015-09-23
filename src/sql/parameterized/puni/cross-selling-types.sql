select distinct ILT.ITEM_LINK_TYPE as TYPE
from CB02{{business}}.ITEMS i1, CB02{{business}}.ITEMS i2, CB02{{business}}.ITEM_LINKS il, CB02{{business}}.ITEM_LINK_TYPES ilt
where IL.ITEM_LINK_TYPE_ID = ILT.ITEM_LINK_TYPE_ID
and I1.ITEM_ID = IL.ITEM_ID
and I2.ITEM_ID = IL.LINKED_ITEM_ID
