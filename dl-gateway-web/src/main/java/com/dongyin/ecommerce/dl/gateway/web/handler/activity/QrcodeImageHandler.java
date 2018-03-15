package com.dongyin.ecommerce.dl.gateway.web.handler.activity;


import com.dongyin.commons.cache.CacheClient;
import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.utils.FileUtil;
import com.dongyin.commons.utils.JsonUtil;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.util.WxOSSClient;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.QrcodeConfigDTO;
import com.dongyin.leaf.wsacd.errors.SalesActivityError;
import com.dongyin.leaf.wsacd.service.iface.ImageQRCodeService;
import com.dongyin.leaf.wsacd.util.QRCodeUtils;
import com.hzkans.leaf.user.dto.MemberDTO;
import com.hzkans.leaf.user.service.iface.MemberService;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @author jc
 * @description
 * @create 2018/3/6
 */
@Service
public class QrcodeImageHandler extends ActivityComplexHandler  {

    private static final Logger log = LoggerFactory.getLogger(QrcodeImageHandler.class);

    private static final String EXTERNAL_ROOT = "qr";

    private static final String TYPE = "jpg";

    private static final String text = "来自 XX 的分享";

    private static final String ADDRESS = "XXX_12345678.png";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;

    private static final Short QR_HEIGHT = 300;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    private static final int MINUTE = 1728000;//10天

    private static final String key = "QRCODE";

    private static final String FILE_PATH = "/deploy/data/www/static/upload/";

    private static final String beiFilePath = "/deploy/data/www/static/upload/background.jpg";

    private static final String touPath = "/deploy/data/www/static/upload/leaf_via.png";

    @Resource
    private ImageQRCodeService imageQRCodeService;

    @Resource
    private MemberService memberService;

    @Autowired
    private WxOSSClient wxOSSClient;

    @Autowired
    private CacheClient cacheClient;
    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response)
            throws BaseBusiException {

        Map<String,Object> requestMap = msg.getMsgFieldMap();

        String content = RequestMapUtils.getStringNotAllowNull(requestMap,"content","content is null");

        Long actId = RequestMapUtils.getLongAllowNull(requestMap,"act_id");

        String openId = RequestMapUtils.getStringNotAllowNull(requestMap,"open_id","open_id is null");

        QrcodeConfigDTO qrcodeConfigDTO = new QrcodeConfigDTO();
        qrcodeConfigDTO.setContent(content);
        qrcodeConfigDTO.setActId(actId);
        qrcodeConfigDTO.setOpenId(openId);
        qrcodeConfigDTO.setWidth(QR_HEIGHT);
        qrcodeConfigDTO.setHeigth(QR_HEIGHT);

        InputStream bis = null;

        try {
            //查询该代理商的图片 有就返回 没有就生成
            String address = getCatch(key+openId);
            log.info("[{}] address :{}",address);
            if(null != address && "" != address) {
                return ResponseUtils.getSuccessResponse(address);
            }
            QrcodeConfigDTO qrcode =imageQRCodeService.getQrcodeConfig(qrcodeConfigDTO);

            if(qrcode != null) {
                String ad = qrcode.getGeneratePic();
                log.info("[{}] ad :{}",ad);
                cacheClient.set(key+openId,MINUTE,ad);
                return ResponseUtils.getSuccessResponse(ad);
            }

            //获取该对象的真实姓名
            MemberDTO memberDTO = memberService.getMembersByOpenId(openId);
            String realName = "";
            Long memberId = null;
            if(null != memberDTO){
                realName = memberDTO.getRealName();
                memberId = memberDTO.getMemberId();
            }

            bis = new FileInputStream(new File(beiFilePath));

            //二维码
            BufferedImage image = QRCodeUtils.generateQRCode(qrcodeConfigDTO);

            BufferedImage bg = ImageIO.read(bis);

            //合成头像
            String ta = FILE_PATH +ADDRESS.replace("XXX",memberId.toString());

            //当file小于6kb的时候,就换为默认头像
            File file = new File(ta);
            if(file.length()/1024 < 6) {
                file = new File(touPath);
            }

            insertImage(image,new FileInputStream(file),true);

            Graphics2D g = bg.createGraphics();

            int width = image.getWidth(null) > bg.getWidth() * 5 / 10 ? (bg.getWidth() * 5 / 10) : image.getWidth(null);

            int height = image.getHeight(null) > bg.getHeight() * 5 / 10 ? (bg.getHeight() * 5 / 10) : image.getWidth(null);

            //合成图片
            Double aa = (bg.getHeight() - height)*0.85;

            g.drawImage(image, (bg.getWidth() - width) /2,aa.intValue()  , null);

        /*g.drawImage(tag, (image.getWidth()) /2, (image.getHeight() - height)/2, null);*/

            //设置混合效果
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

            //设置颜色。
            //g.setColor(Color.BLACK);

            //最后一个参数用来设置字体的大小
            Font f = new Font("Microsoft YaHei", Font.PLAIN, 40);
            Color mycolor = Color.white;//new Color(0, 0, 255);
            g.setColor(mycolor);
            g.setFont(f);

            //表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
            String word = text.replace("XX",realName);
            g.drawString(word, (bg.getWidth() - width) / 2, (bg.getHeight() - height) * 10/ 12);

            g.dispose();

            bg.flush();

            image.flush();

            File folder = new File(FILE_PATH);  //测试环境调试
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String hName = System.currentTimeMillis()+ UUID.randomUUID().toString()+".jpg";
            String hFilePath = FILE_PATH + hName;
            log.info("[{}] path :{}",hFilePath);

            //插入数据
            qrcodeConfigDTO.setGeneratePic(hName);
            qrcodeConfigDTO.setText(word);
            log.info("[{}] qrcode:{}", JsonUtil.toJson(qrcodeConfigDTO));
            imageQRCodeService.insert(qrcodeConfigDTO);

            //存入缓存中
            cacheClient.set(key+openId,MINUTE,hName);

            //先上传到服务器,在上传到OSS,然后删除服务器上的文件
            ImageIO.write(bg, TYPE, new File(hFilePath));

            //上传到oss
            wxOSSClient.uploadFile(EXTERNAL_ROOT,hName,hFilePath);

            // 上传完之后删除;
            FileUtil.destroyFile(hName);
            return ResponseUtils.getSuccessResponse(hName);


        } catch (Exception e) {
            log.error("generatePicture error ");
            return ResponseUtils.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION,e.getMessage());
        }

    }


    private void insertImage(BufferedImage source, InputStream imgPath,
                             boolean needCompress) throws Exception {
        /*File file = new File(imgPath);
        if (!file.exists()) {
            log.error(imgPath + "  该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));*/
        Image src = ImageIO.read(imgPath);
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    private String getCatch(String key) throws Exception{

        Object obj = cacheClient.get(key);

        if(obj != null) {
            return (String) obj;
        }
        return null;
    }
}
