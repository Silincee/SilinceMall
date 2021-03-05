package cn.silince.silincemall.member.service.impl;

import cn.silince.common.utils.HttpUtils;
import cn.silince.silincemall.member.dao.MemberLevelDao;
import cn.silince.silincemall.member.entity.MemberLevelEntity;
import cn.silince.silincemall.member.exception.PhoneExistException;
import cn.silince.silincemall.member.exception.UsernameExistException;
import cn.silince.silincemall.member.vo.MemberLoginVo;
import cn.silince.silincemall.member.vo.MemberRegistVo;
import cn.silince.silincemall.member.vo.SocialUserVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.Query;

import cn.silince.silincemall.member.dao.MemberDao;
import cn.silince.silincemall.member.entity.MemberEntity;
import cn.silince.silincemall.member.service.MemberService;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity entity = new MemberEntity();

        // 设置默认等级
        MemberLevelEntity levelEntity =memberLevelDao.getDefaultLevel();
        entity.setLevelId(levelEntity.getId());

        // 检查用户名和手机号是否唯一。 为了让Controller感知异常，可以使用异常机制
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUsername());

        entity.setMobile(vo.getPhone());
        entity.setUsername(vo.getUsername());
        entity.setNickname(vo.getUsername());

        // 加密存储
        String encode = new BCryptPasswordEncoder().encode(vo.getPassword());
        entity.setPassword(encode);

        // 其他的默认信息...

        // 保存
        this.baseMapper.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        Integer count = this.baseMapper.selectCount(
                new QueryWrapper<MemberEntity>().eq("mobile", phone)
        );
        if (count>0){
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) throws  UsernameExistException{
        Integer count = this.baseMapper.selectCount(
                new QueryWrapper<MemberEntity>().eq("username", username)
        );
        if (count>0){
        throw new UsernameExistException();}
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String account = vo.getLoginacct();
        String password = vo.getPassword();

        // 1 去数据库查询
        MemberEntity entity = this.baseMapper.selectOne(
                new QueryWrapper<MemberEntity>().eq("username", account)
                        .or().eq("mobile", account)
        );
        if (entity==null){
            // 登陆失败
            return null;
        }else {
            // 比较密码
            String passwordFromDb = entity.getPassword();
            boolean matches = new BCryptPasswordEncoder().matches(password, passwordFromDb);
            if (matches){
                // 登陆成功
                return entity;
            }else {
                return null;
            }

        }

    }

    /**
    * @description: 登陆和组合合并逻辑
    */
    @Override
    public MemberEntity login(SocialUserVo vo) throws Exception {

        //1 判断当前社交用户是否在已经登陆过系统
        MemberEntity memberEntity = this.baseMapper.selectOne(
                new QueryWrapper<MemberEntity>().eq("social_uid", vo.getUid())
        );
        if (memberEntity!=null){
            // 2 已注册 只需要更换令牌
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(vo.getAccess_token());
            update.setExpiresIn(vo.getExpires_in());

            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(vo.getAccess_token());
            memberEntity.setExpiresIn(vo.getExpires_in());
            return memberEntity;
        }else {
            // 没有查到当前社交账户  创建一个新用户
            MemberEntity regist=new MemberEntity();
            try {
                // 3 查询当前社交用户的社交账号信息(昵称，性别等)
                HashMap<String, String> query = new HashMap<>();
                query.put("access_token",vo.getAccess_token());
                query.put("uid",vo.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get",
                        new HashMap<String, String>(), query);
                if (response.getStatusLine().getStatusCode()==200){
                    // 查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name"); // 昵称
                    String gender = jsonObject.getString("gender"); // 性别
                    regist.setNickname(name);
                    regist.setGender("m".equals(gender)?1:0);
                }
            } catch (Exception e) { }
            regist.setSocialUid(vo.getUid());
            regist.setAccessToken(vo.getAccess_token());
            regist.setExpiresIn(vo.getExpires_in());
            this.baseMapper.insert(regist);

            return regist;

        }

    }
}