0.7.3
1.Refactored codes to make it easier to use other type of weapon

0.7.0
1. add setDamage/getDamage
2. add easy bow model
3. add pull tick
4. add release 
5. add get arrow in projectile hit
6. add onUse and hide some method

0.7.0b
1. add functionality of modifying existing bows
2. add speed scale
3. hide set/get bowProperties
4. fix pull
5. fix infinity

0.6.5b
1. add hit block

0.6.4.3a
1. remove redundant subclasses
2. add hitEntity

0.6.4.2a
1. put all `@ReMapForJS` into mixin package
2. rename `fullChargeTicks` to `fullChargeTick`
and add it to vanilla bow
3. change the basic working mechanism from subclass overriding to mixin injection.

0.6.4.1a
1. change the null check in doPostHurtEffect into Optional.
2. change the timing when setting `HitConsumerContainer`
to make the value accessible upon entity spawn.

0.6.4b uploaded