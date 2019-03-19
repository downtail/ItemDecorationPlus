# ItemDecorationPlus

### 介绍

在工作中多次遇到需要为RecyclerView设置悬浮或者说粘性的头部，于是有了这个东西。目前有MaskedItemDecoration(使item本身可悬浮)和CosmeticItemDecoration(在item顶部悬浮一个额外的view)，并且已经适用于LinearLayoutManager和GridLayoutManager(网格布局支持setSpanSizeLookup()，看起来像线性和网格混用)。还有一点非常重要的是，如果搭配
    [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
可以实现更加丰富的效果，比如类似QQ好友列表的分组悬浮以及其他一些需求。

### 效果预览



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

implementation 'com.github.downtail:ItemDecorationPlus:0.1.2'

```

3. 实现接口(让adapter来实现)

```

public class MaskedAdapter extends RecyclerView.Adapter<MaskedAdapter.SampleHolder> implements MaskedExtension{}

```


```

public interface MaskedExtension {

    //返回true则需要实现粘性
    boolean isMaskedItem(int position);

    //粘性view的高度
    int getMaskedHeight(int position);

    //自定义粘性view的布局
    View getMaskedView(int position);

  }
  
```


4. 为RecyclerView添加ItemDecoration

```

MaskedItemDecoration maskedItemDecoration = MaskedItemDecoration.Builder
                .with(maskedAdapter)
                .setOnMaskedItemClickListener(new MaskedItemDecoration.OnMaskedItemClickListener() {
                    @Override
                    public void onMaskedItemClick(int position) {//监听粘性item点击事件
                        Toast.makeText(MaskedActivity.this, "你点击了item " + position, Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnMaskedViewClickListener(new MaskedItemDecoration.OnMaskedViewClickListener() {//监听粘性item中子view点击事件
                    @Override
                    public void onMaskedViewClick(View view, int position) {
                        if (view.getId() == R.id.tv_sample) {
                            Toast.makeText(MaskedActivity.this, "你点击了sample" + position, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();

        rvSample.addItemDecoration(maskedItemDecoration);

```

5. 以上为MaskedItemDecoration使用，CosmeticItemDecoration用法相同。  
  
     
     

### 建议和意见  
如果有什么问题或者新的想法可以issue或者联系我。


### 参考

[StickyDecoration](https://github.com/Gavin-ZYX/StickyDecoration)


