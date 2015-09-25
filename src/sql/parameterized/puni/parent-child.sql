select I1.ITEM as PARENT, I2.ITEM as CHILD
from CB02{{business}}.ITEMS i1, CB02{{business}}.ITEM_CHAINS ic, CB02{{business}}.ITEMS i2
where I1.ITEM_ID = IC.PARENT_ID
and IC.ITEM_ID = I2.ITEM_ID
and I2.ITEM_KIND = 'I'
order by PARENT
