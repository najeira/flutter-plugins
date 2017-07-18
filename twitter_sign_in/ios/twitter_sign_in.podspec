#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'twitter_sign_in'
  s.version          = '0.0.1'
  s.summary          = 'A Flutter plugin for Twitter Sign In.'
  s.description      = <<-DESC
A new flutter plugin project.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'najeira' => 'najeira@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'Fabric'
  s.dependency 'TwitterCore'
  s.dependency 'TwitterKit'
  
  s.ios.deployment_target = '8.0'
end

