# ItemDecorationPlus

### 介绍

在工作中多次遇到需要为RecyclerView设置悬浮或者说粘性的头部，于是有了这个东西。目前有MaskedItemDecoration(使item本身可悬浮)和CosmeticItemDecoration(在item顶部悬浮一个额外的view)，并且已经适用于LinearLayoutManager和GridLayoutManager(网格布局支持setSpanSizeLookup()，看起来像线性和网格混用)。还有一点非常重要的是，如果搭配
    [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
可以实现更加丰富的效果，比如类似QQ好友列表的分组悬浮以及其他一些需求。

### 效果预览



[普通item悬浮](https://s2.ax1x.com/2019/03/19/AnRb9A.gif)

[树形](https://s2.ax1x.com/2019/03/19/AnWGDK.gif)

[混合item](https://s2.ax1x.com/2019/03/19/AnWU4H.gif)



### 使用方式

1. 添加jitpack仓库  
```

allprojects {  
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

```
  
2. 添加依赖

```

implementation 'com.github.downtail:ItemDecorationPlus:0.2.0'

```

3. 实现接口(让adapter来实现)

```

public class MaskedAdapter extends RecyclerView.Adapter<MaskedAdapter.SampleHolder> implements FloaterExtension{}

```


```

public interface FloaterExtension {

    //是否悬浮
    boolean isFloaterView(int position);

    //悬浮的itemType
    @IntRange(from = 0, to = Integer.MAX_VALUE)
    int getItemType(int position);
}
  
```


4. 为RecyclerView添加ItemDecoration

```

FloaterView floaterView = FloaterView.init(rvExhibition)
                .addItemType(ExhibitionMultipleEntity.ITEM_HEADER,R.layout.item_basket_exhibition_header)
                .setOnBindViewListener(new FloaterView.OnBindViewListener() {
                    @Override
                    public void onBind(View view, int position) {
                        
                    }
                })
                .setOnItemChildClickListener(new FloaterView.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(View view, int position) {
                        
                    }
                });
        FloaterItemDecoration floaterItemDecoration = new FloaterItemDecoration(exhibitionMultipleAdapter, floaterView);

```
5. 具体使用参考demo  

6. 以上即为使用方法。  
  
     
     

### 建议和意见  
如果有什么问题或者新的想法可以issue或者联系我。


### 参考

[StickyDecoration](https://github.com/Gavin-ZYX/StickyDecoration)


