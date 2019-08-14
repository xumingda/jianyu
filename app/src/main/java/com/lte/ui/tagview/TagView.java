//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lte.ui.tagview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lte.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TagView extends LinearLayout {
    private List<Tag> mTags = new ArrayList();
    private LayoutInflater mInflater;
    private ViewTreeObserver mViewTreeObserber;
    private TagView.OnTagClickListener mClickListener;
    private TagView.OnTagDeleteListener mDeleteListener;
    private TagView.OnTagLongClickListener mTagLongClickListener;
    private int mWidth;
    private boolean mInitialized = false;
    private int lineMargin;
    private int tagMargin;
    private int textPaddingLeft;
    private int textPaddingRight;
    private int textPaddingTop;
    private int textPaddingBottom;

    public TagView(Context ctx) {
        super(ctx, null);
        this.initialize(ctx, null, 0);
    }

    public TagView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        this.initialize(ctx, attrs, 0);
    }

    public TagView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        this.initialize(ctx, attrs, defStyle);
    }

    private void initialize(Context ctx, AttributeSet attrs, int defStyle) {
        setOrientation(LinearLayout.VERTICAL);
        this.mInflater = LayoutInflater.from(ctx);
        this.mViewTreeObserber = this.getViewTreeObserver();
        this.mViewTreeObserber.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (!TagView.this.mInitialized) {
                    TagView.this.mInitialized = true;
                    TagView.this.drawTags();
                }

            }
        });
        TypedArray typeArray = ctx.obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, defStyle);
        this.lineMargin = (int) typeArray.getDimension(R.styleable.TagView_lineMargin, (float) Utils.dipToPx(this.getContext(), 5.0F));
        this.tagMargin = (int) typeArray.getDimension(R.styleable.TagView_tagMargin, (float) Utils.dipToPx(this.getContext(), 5.0F));
        this.textPaddingLeft = (int) typeArray.getDimension(R.styleable.TagView_textPaddingLeft, (float) Utils.dipToPx(this.getContext(), 8.0F));
        this.textPaddingRight = (int) typeArray.getDimension(R.styleable.TagView_textPaddingRight, (float) Utils.dipToPx(this.getContext(), 8.0F));
        this.textPaddingTop = (int) typeArray.getDimension(R.styleable.TagView_textPaddingTop, (float) Utils.dipToPx(this.getContext(), 5.0F));
        this.textPaddingBottom = (int) typeArray.getDimension(R.styleable.TagView_textPaddingBottom, (float) Utils.dipToPx(this.getContext(), 5.0F));
        typeArray.recycle();
    }

    public void setInitialized(boolean isInitialized) {
        this.mInitialized = isInitialized;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = this.getMeasuredWidth();
        if (width > 0) {
            this.mWidth = this.getMeasuredWidth();
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawTags();
    }

    private void drawTags() {
        if (this.mInitialized) {
            this.removeAllViews();
            float total = (float) (this.getPaddingLeft() + this.getPaddingRight());
            int listIndex = 1;
            int indexBottom = 1;
            int indexHeader = 1;
            Tag tagPre = null;

            for (Iterator var6 = this.mTags.iterator(); var6.hasNext(); ++listIndex) {
                final Tag item = (Tag) var6.next();
                final int position = listIndex - 1;
                View tagLayout = this.mInflater.inflate(R.layout.tagview_item, null);
//                tagLayout.setId(listIndex);
                if (VERSION.SDK_INT < 16) {
                    tagLayout.setBackgroundDrawable(this.getSelector(item));
                } else {
                    tagLayout.setBackground(this.getSelector(item));
                }

                TextView tagView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_contain);
                tagView.setText(item.text);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tagView.getLayoutParams();
                params.setMargins(this.textPaddingLeft, this.textPaddingTop, this.textPaddingRight, this.textPaddingBottom);
                tagView.setLayoutParams(params);
                tagView.setTextColor(item.tagTextColor);
                tagView.setTextSize(2, item.tagTextSize);
                tagLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (TagView.this.mClickListener != null) {
                            TagView.this.mClickListener.onTagClick(item, position);
                        }

                    }
                });
                tagLayout.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        if (TagView.this.mTagLongClickListener != null) {
                            TagView.this.mTagLongClickListener.onTagLongClick(item, position);
                        }

                        return true;
                    }
                });
                float tagWidth = tagView.getPaint().measureText(item.text) + (float) this.textPaddingLeft + (float) this.textPaddingRight;
                TextView deletableView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_delete);
                if (item.isDeletable) {
                    deletableView.setVisibility(View.VISIBLE);
                    deletableView.setText(item.deleteIcon);
                    int tagParams = Utils.dipToPx(this.getContext(), 2.0F);
                    deletableView.setPadding(tagParams, this.textPaddingTop, this.textPaddingRight + tagParams, this.textPaddingBottom);
                    deletableView.setTextColor(item.deleteIndicatorColor);
                    deletableView.setTextSize(2, item.deleteIndicatorSize);
                    deletableView.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (TagView.this.mDeleteListener != null) {
                                TagView.this.mDeleteListener.onTagDeleted(TagView.this, item, position);
                            }

                        }
                    });
                    tagWidth += deletableView.getPaint().measureText(item.deleteIcon) + (float) this.textPaddingLeft + (float) this.textPaddingRight;
                } else {
                    deletableView.setVisibility(View.GONE);
                }

                LayoutParams layoutParams = new LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.bottomMargin = this.lineMargin;
//                if ((float) this.mWidth <= total + tagWidth + (float) Utils.dipToPx(this.getContext(), 2.0F)) {
//                    layoutParams.addRule(RelativeLayout.BELOW, indexBottom);
//                    total = (float) (this.getPaddingLeft() + this.getPaddingRight());
//                    indexBottom = listIndex;
//                    indexHeader = listIndex;
//                } else {
//                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, indexHeader);
//                    if (listIndex != indexHeader) {
//                        layoutParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
//                        layoutParams.leftMargin = this.tagMargin;
//                        total += (float) this.tagMargin;
//                        if (tagPre.tagTextSize < item.tagTextSize) {
//                            indexBottom = listIndex;
//                        }
//                    }
//                }

//                total += tagWidth;
                this.addView(tagLayout, layoutParams);
                tagPre = item;
            }

        }
    }

    private Drawable getSelector(Tag tag) {
        if (tag.background != null) {
            return tag.background;
        } else {
            StateListDrawable states = new StateListDrawable();
            GradientDrawable gdNormal = new GradientDrawable();
            gdNormal.setColor(tag.layoutColor);
            gdNormal.setCornerRadius(tag.radius);
            if (tag.layoutBorderSize > 0.0F) {
                gdNormal.setStroke(Utils.dipToPx(this.getContext(), tag.layoutBorderSize), tag.layoutBorderColor);
            }

            GradientDrawable gdPress = new GradientDrawable();
            gdPress.setColor(tag.layoutColorPress);
            gdPress.setCornerRadius(tag.radius);
            states.addState(new int[]{16842919}, gdPress);
            states.addState(new int[0], gdNormal);
            return states;
        }
    }

    public void addTag(Tag tag) {
        this.mTags.add(tag);
        this.drawTags();
    }
    public void addTags(List<Tag> tags) {
        if (tags != null) {
            this.mTags = new ArrayList();
            if (tags.isEmpty()) {
                this.drawTags();
            }

            Iterator var2 = tags.iterator();

            while (var2.hasNext()) {
                Tag item = (Tag) var2.next();
                this.addTag(item);
            }

        }
    }

    public void addTags(String[] tags) {
        if (tags != null) {
            String[] var2 = tags;
            int var3 = tags.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String item = var2[var4];
                Tag tag = new Tag(item);
                this.addTag(tag);
            }

        }
    }

    public List<Tag> getTags() {
        return this.mTags;
    }

    public void remove(int position) {
        if (position < this.mTags.size()) {
            this.mTags.remove(position);
            this.drawTags();
        }

    }

    public void removeAll() {
        this.mTags.clear();
        this.removeAllViews();
    }

    public int getLineMargin() {
        return this.lineMargin;
    }

    public void setLineMargin(float lineMargin) {
        this.lineMargin = Utils.dipToPx(this.getContext(), lineMargin);
    }

    public int getTagMargin() {
        return this.tagMargin;
    }

    public void setTagMargin(float tagMargin) {
        this.tagMargin = Utils.dipToPx(this.getContext(), tagMargin);
    }

    public int getTextPaddingLeft() {
        return this.textPaddingLeft;
    }

    public void setTextPaddingLeft(float textPaddingLeft) {
        this.textPaddingLeft = Utils.dipToPx(this.getContext(), textPaddingLeft);
    }

    public int getTextPaddingRight() {
        return this.textPaddingRight;
    }

    public void setTextPaddingRight(float textPaddingRight) {
        this.textPaddingRight = Utils.dipToPx(this.getContext(), textPaddingRight);
    }

    public int getTextPaddingTop() {
        return this.textPaddingTop;
    }

    public void setTextPaddingTop(float textPaddingTop) {
        this.textPaddingTop = Utils.dipToPx(this.getContext(), textPaddingTop);
    }

    public int gettextPaddingBottom() {
        return this.textPaddingBottom;
    }

    public void settextPaddingBottom(float textPaddingBottom) {
        this.textPaddingBottom = Utils.dipToPx(this.getContext(), textPaddingBottom);
    }

    public void setOnTagLongClickListener(TagView.OnTagLongClickListener longClickListener) {
        this.mTagLongClickListener = longClickListener;
    }

    public void setOnTagClickListener(TagView.OnTagClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setOnTagDeleteListener(TagView.OnTagDeleteListener deleteListener) {
        this.mDeleteListener = deleteListener;
    }

    public interface OnTagLongClickListener {
        void onTagLongClick(Tag var1, int var2);
    }

    public interface OnTagClickListener {
        void onTagClick(Tag var1, int var2);
    }

    public interface OnTagDeleteListener {
        void onTagDeleted(TagView var1, Tag var2, int var3);
    }
}
