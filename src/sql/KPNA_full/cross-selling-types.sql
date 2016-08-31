select distinct CST.CROSS_SELL_TYPE_UC as TYPE
from CB02KPNA.ITEM_INFO ii, CB02KPNA.CROSS_SELLING cs, CB02KPNA.CROSS_SELLING_TYPES cst
where II.ITEM_INFO_ID = CS.ITEM_INFO_ID
and CS.CROSS_SELL_TYPE_ID = CST.CROSS_SELL_TYPE_ID
